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
    Ok( views.html.interview.intro(fcs,dtt) )
      .withSession( session + ("uuid" -> userSession.key) )
  }

  def startInterview( questionnaireId:String ) = UserSessionAction { implicit req =>
      val interview = global.Global.interview
      val rte = new RuntimeEngine
      rte.setChartSet( interview )
      val l = rte.setListener( new TaggingEngineListener )
      rte.start( interview.getDefaultChartId )
      val updated = req.userSession.updatedWith( l.traversedNodes, rte.createSnapshot )
      Cache.set(req.userSession.key, updated)
      
      Ok( views.html.interview.question(questionnaireId, 
                                         rte.getCurrentNode.asInstanceOf[AskNode],
                                         updated.tags,
                                         l.traversedNodes,
                                         Seq()) )
  }

  def askNode( nodeId: String ) = UserSessionAction { req =>
    val flowChartId = req.userSession.engineState.getCurrentChartId
    val nodeId = req.userSession.engineState.getCurrentNodeId
    val askNode = global.Global.interview.getFlowChart(flowChartId).getNode(nodeId).asInstanceOf[AskNode]
    Ok( views.html.interview.question( "id",
                                       askNode,
                                       req.userSession.tags,
                                       req.userSession.traversed,
                                       req.userSession.answerHistory) )
  }

  def answer(questionnaireId: String, nodeId: String) = UserSessionAction { implicit request =>
     val userSession = request.userSession
     val answerForm = Form( tuple("answerText"->text, "nodeId"->text) )
     answerForm.bindFromRequest().fold (
      failed => Ok("Form submission error %s\n data:%s".format( failed.errors, failed.data)),
      { value  => 
        
        val answer = new Answer( value._1 )
        val ansRec = AnswerRecord( currentAskNode(userSession.engineState), answer)
        val runRes = advanceEngine( userSession.engineState, answer )
        Cache.set( userSession.key, userSession.updatedWith( ansRec, runRes.traversed,runRes.state))
        val status = runRes.state.getStatus
        status match {
          case RuntimeEngineStatus.Running => Redirect( routes.Interview.askNode( runRes.state.getCurrentNodeId ) )
          case RuntimeEngineStatus.Reject  => Redirect( routes.Interview.reject( questionnaireId ) )
          case RuntimeEngineStatus.Accept  => Redirect( routes.Interview.accept( questionnaireId ) )
          case _ => InternalServerError("Bad interview state")
    }})
  }

  def accept( questionnaireId:String ) = UserSessionAction { request =>
    val tags = request.userSession.tags
    val code = Option(tags.get( tags.getType.getTypeNamed("code") ))
    Ok( views.html.interview.accepted(questionnaireId, tags, code)  )  
  }

  def reject( questionnaireId:String ) = UserSessionAction { request =>
    val state = request.userSession.engineState
    val node = global.Global.interview.getFlowChart( state.getCurrentChartId ).getNode( state.getCurrentNodeId )

    Ok( views.html.interview.rejected(questionnaireId, node.asInstanceOf[RejectNode].getReason) )
  }

  def revisit( nodeId: String ) = UserSessionAction { request =>
    val userSession = request.userSession
    val updatedState = runUpToNode( null, nodeId, userSession.answerHistory )
    val answers = userSession.answerHistory.slice(0, userSession.answerHistory.indexWhere(_.question.getId == nodeId) )
    Cache.set( userSession.key,
               userSession.replaceHistory( answers, updatedState.traversed, updatedState.state ) )

    Redirect( routes.Interview.askNode(nodeId) )
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
  def runUpToNode( state: RuntimeEngineState, nodeId: String, answers:Seq[AnswerRecord] ) : EngineRunResult = {
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
