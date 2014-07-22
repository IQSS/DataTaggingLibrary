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

/**
 * Controller for the interview part of the application.
 */
object Interview extends Controller {

  def interviewIntro(questionnaireId: String) = Action { implicit request =>

    val userSession = UserSession.create( questionnaireId )

    Cache.set(userSession.key, userSession)
    val fcs = global.Global.interview
    val dtt = global.Global.dataTags
    Ok( views.html.interview.intro(fcs,dtt, Option(null) ))
      .withSession( session + ("uuid" -> userSession.key) )
  }

  def startInterview( questionnaireId:String ) = UserSessionAction { implicit req =>
      val interview = global.Global.interview
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

  def askNode( questionnaireId:String, reqNodeId: String ) = UserSessionAction { req =>
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

    val askNode = global.Global.interview.getFlowChart(flowChartId).getNode(reqNodeId).asInstanceOf[AskNode]
    Ok( views.html.interview.question( "questionnaireId",
                                       askNode,
                                       session.tags,
                                       session.traversed,
                                       session.answerHistory) )
  }

  def answer(questionnaireId: String, reqNodeId: String) = UserSessionAction { implicit request =>
      val session = if ( request.userSession.engineState.getCurrentNodeId != reqNodeId ) {
      // re-run to reqNodeId
      val answers = request.userSession.answerHistory.slice(0, request.userSession.answerHistory.indexWhere( _.question.getId == reqNodeId) )
      val rerunResult = runUpToNode( reqNodeId, answers )
      request.userSession.replaceHistory( answers, rerunResult.traversed, rerunResult.state )

    } else {
      request.userSession
    }

    val answerForm = Form( "answerText"->text )
    answerForm.bindFromRequest().fold (
      { failed => BadRequest("Form submission error: %s\n data:%s".format(failed.errors, failed.data)) },

      { value  => 
        val answer = new Answer( value )
        val ansRec = AnswerRecord( currentAskNode(session.engineState), answer)
        val runRes = advanceEngine( session.engineState, answer )
        Cache.set( session.key, session.updatedWith( ansRec, runRes.traversed,runRes.state))
        val status = runRes.state.getStatus
        status match {
          case RuntimeEngineStatus.Running => Redirect( routes.Interview.askNode( questionnaireId, runRes.state.getCurrentNodeId ) )
          case RuntimeEngineStatus.Reject  => Redirect( routes.Interview.reject( questionnaireId ) )
          case RuntimeEngineStatus.Accept  => Redirect( routes.Interview.accept( questionnaireId ) )
          case _ => InternalServerError("Bad interview state")
    }})
  }

  def accept( questionnaireId:String ) = UserSessionAction { request =>
    val session = request.userSession
    val tags = session.tags
    val code = Option(tags.get( tags.getType.getTypeNamed("Code") ))
    Ok( views.html.interview.accepted(questionnaireId, tags, code, session.requestedInterview, session.answerHistory )  )  
  }

  def reject( questionnaireId:String ) = UserSessionAction { request =>
    val state = request.userSession.engineState
    val node = global.Global.interview.getFlowChart( state.getCurrentChartId ).getNode( state.getCurrentNodeId )

    Ok( views.html.interview.rejected(questionnaireId, node.asInstanceOf[RejectNode].getReason) )
  }

  def revisit( nodeId: String ) = UserSessionAction { request =>
    val userSession = request.userSession
    val updatedState = runUpToNode( nodeId, userSession.answerHistory )
    val answers = userSession.answerHistory.slice(0, userSession.answerHistory.indexWhere(_.question.getId == nodeId) )
    Cache.set( userSession.key,
               userSession.replaceHistory( answers, updatedState.traversed, updatedState.state ) )

    Redirect( routes.Interview.askNode(userSession.questionnaireId, nodeId) )
  }

  // TODO: move to some akka actor, s.t. the UI can be reactive
  def advanceEngine( state: RuntimeEngineState, ans: Answer ) : EngineRunResult = {
    val interview = global.Global.interview
    val rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    rte.applySnapshot( state )
    rte.consume( ans )

    return EngineRunResult( rte.createSnapshot, l.traversedNodes, l.exception )

  }

  // TODO: move to some akka actor, s.t. the UI can be reactive
  def runUpToNode( nodeId: String, answers:Seq[AnswerRecord] ) : EngineRunResult = {
    val interview = global.Global.interview
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

  def currentAskNode( engineState: RuntimeEngineState ) = {
    global.Global.interview.getFlowChart(engineState.getCurrentChartId).getNode(engineState.getCurrentNodeId).asInstanceOf[AskNode]
  }

}
