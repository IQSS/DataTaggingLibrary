package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._

import edu.harvard.iq.datatags.runtime._
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._

import models._
import _root_.util.Jsonizer

import java.text.SimpleDateFormat

import play.api.Logger


/**
 * Controller for the interview part of the application.
 */
object Interview extends Controller {

  def interviewIntro(questionnaireId: String) = Action { implicit request =>
    val userSession = UserSession.create( questionnaireId )

    Cache.set(userSession.key, userSession)
    Ok( views.html.interview.intro(
          QuestionnaireKits.kit.questionnaire,
          QuestionnaireKits.kit.tags,
          Option(null) )
      ).withSession( request2session + ("uuid" -> userSession.key) )
  }

  def startInterview( questionnaireId:String ) = UserSessionAction { implicit req =>
      val interview = QuestionnaireKits.kit.questionnaire
      val rte = new RuntimeEngine
      rte.setChartSet( interview )
      val l = rte.setListener( new TaggingEngineListener )
      rte.start( interview.getDefaultChartId )
      val updated = req.userSession.replaceHistory( Seq[AnswerRecord](), l.traversedNodes, rte.createSnapshot )
      Cache.set(req.userSession.key, updated)
      
      Ok( views.html.interview.question(questionnaireId, 
                                         rte.getCurrentNode.asInstanceOf[AskNode],
                                         updated.tags,
                                         l.traversedNodes,
                                         Seq()) )
  }

  def askNode( questionnaireId:String, reqNodeId: String) = UserSessionAction { req =>
    // TODO validate questionnaireId fits the one in the engine state
    val flowChartId = req.userSession.engineState.getCurrentChartId
    val stateNodeId = req.userSession.engineState.getCurrentNodeId
    
    val session = if ( stateNodeId != reqNodeId ) {
      // re-run to reqNodeId
      val answers = req.userSession.answerHistory.slice(0, req.userSession.answerHistory.indexWhere( _.question.getId == reqNodeId) )
      val rerunResult = runUpToNode( reqNodeId, answers )
      val updatedSession = req.userSession.replaceHistory( answers, rerunResult.traversed, rerunResult.state )
      Cache.set( req.userSession.key, updatedSession )
      updatedSession
    
    } else {
      req.userSession
    }

    val askNode = QuestionnaireKits.kit.questionnaire.getFlowChart(flowChartId).getNode(reqNodeId).asInstanceOf[AskNode]

    Ok( views.html.interview.question( "questionnaireId",
                                       askNode,
                                       session.tags,
                                       session.traversed,
                                       session.answerHistory) )
  }

  case class AnswerRequest( text:String, history:String )
  def answer(questionnaireId: String, reqNodeId: String) = UserSessionAction { implicit request =>
    val arForm = Form( mapping(
        "answerText" -> text,
        "serializedHistory"->text
        )(AnswerRequest.apply)(AnswerRequest.unapply) )

    arForm.bindFromRequest.fold(
      { failed => BadRequest("Form submission error: %s\n data:%s".format(failed.errors, failed.data)) },
      { answerReq => 
        // See if we can re-use the session data we have.
        // LATER - test index rather than node id, to allow loops.
        val session = if ( request.userSession.engineState.getCurrentNodeId == reqNodeId ) {
          // yes
          request.userSession
        } else {
          // no, rebuild from serialized history
          QuestionnaireKits.kit.serializer.decode(answerReq.history, request.userSession)
        }

        // now, submit the new answer and feed it to the engine.
        val answer = new Answer( answerReq.text )
        val ansRec = AnswerRecord( currentAskNode(session.engineState), answer )
        val runRes = advanceEngine( session.engineState, answer )

        // save state and decide where to go from here
        Cache.set( session.key, session.updatedWith( ansRec, runRes.traversed, runRes.state))
        runRes.state.getStatus match {
          case RuntimeEngineStatus.Running => Redirect( routes.Interview.askNode( questionnaireId, runRes.state.getCurrentNodeId ) )
          case RuntimeEngineStatus.Reject  => Redirect( routes.Interview.reject( questionnaireId ) )
          case RuntimeEngineStatus.Accept  => Redirect( routes.Interview.accept( questionnaireId ) )
          case _ => InternalServerError("Bad interview state")
        }
      }
    )
  }

  /**
   * Re-run up to the index of the question, so the user gets to answer it again.
   */
  case class RevisitRequest( history:String, idx:Int )

  def revisit( questionnaireId: String ) = UserSessionAction { implicit request =>

    val revReqForm = Form( mapping(
                              "serializedHistory"->text, 
                              "revisitIdx"->number
                            )(RevisitRequest.apply)(RevisitRequest.unapply)
                          )
    revReqForm.bindFromRequest.fold(
      failure => BadRequest("Form submission error: %s\n data:%s".format(failure.errors, failure.data)),
      revisitRequest => {
        val updatedSession = QuestionnaireKits.kit.serializer.decode(revisitRequest.history.take(revisitRequest.idx),
                                                                     request.userSession)

        Cache.set( updatedSession.key, updatedSession )
        Redirect( routes.Interview.askNode( questionnaireId, updatedSession.engineState.getCurrentNodeId ) ) 
      }
    )
  }

  def accept( questionnaireId:String ) = UserSessionAction { request =>
    val session = request.userSession
    val tags = session.tags
    val code = Option(tags.get( tags.getType.getTypeNamed("Code") ))
    
    Ok( views.html.interview.accepted(questionnaireId, tags, code, session.requestedInterview, session.answerHistory )  )
  }

  def reject( questionnaireId:String ) = UserSessionAction { request =>
    val session = request.userSession
    val state = request.userSession.engineState
    val node = QuestionnaireKits.kit.questionnaire.getFlowChart( state.getCurrentChartId ).getNode( state.getCurrentNodeId )

    Ok( views.html.interview.rejected(questionnaireId, node.asInstanceOf[RejectNode].getReason, session.requestedInterview, session.answerHistory ) )
  }

  // TODO: move to some akka actor, s.t. the UI can be reactive
  def advanceEngine( state: RuntimeEngineState, ans: Answer ) : EngineRunResult = {
    val interview = QuestionnaireKits.kit.questionnaire
    val rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    rte.applySnapshot( state )
    rte.consume( ans )
	
    EngineRunResult( rte.createSnapshot, l.traversedNodes, l.exception )

  }

  // TODO: move to some akka actor, s.t. the UI can be reactive
  def runUpToNode( nodeId: String, answers:Seq[AnswerRecord] ) : EngineRunResult = {
    val interview = QuestionnaireKits.kit.questionnaire
    val rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    val ansItr = answers.iterator
    
    rte.start( interview.getDefaultChartId )
    
    while ( rte.getCurrentNode.getId != nodeId ) {
      val answer = ansItr.next.answer
      rte.consume( answer )
    }

    return EngineRunResult( rte.createSnapshot, l.traversedNodes, l.exception )

  }

  /**
   * Run the engine, from the start, through all the answer sequence passed.
   */
  // TODO: move to some akka actor, s.t. the UI can be reactive
  def replayAnswers( answers:Seq[AnswerRecord] ) : EngineRunResult = {
    val interview = QuestionnaireKits.kit.questionnaire
    val rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    
    rte.start( interview.getDefaultChartId )
    answers.map( _.answer )
           .foreach( rte.consume(_) )

    return EngineRunResult( rte.createSnapshot, l.traversedNodes, l.exception )

  }

  def currentAskNode( engineState: RuntimeEngineState ) = {
    QuestionnaireKits.kit.questionnaire.getFlowChart(engineState.getCurrentChartId).getNode(engineState.getCurrentNodeId).asInstanceOf[AskNode]
  }

  def downloadTags = UserSessionAction { request =>
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val filename =  QuestionnaireKits.kit.title + "-" + dateFormat.format(request.userSession.sessionStart)
    Ok(request.userSession.tags.accept(Jsonizer))
      .withHeaders( "Content-disposition" -> ("attachment; filename=\"%s\"".format(filename) ))
  }

}
