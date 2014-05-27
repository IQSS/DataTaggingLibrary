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
      val updated = req.userSession.copy( engineState = rte.createSnapshot(),
                                      traversed = l.traversedNodes )
      Cache.set(req.userSession.key, updated)
      
      Ok( views.html.interview.question(questionnaireId, 
                                         rte.getCurrentNode.asInstanceOf[AskNode],
                                         updated.tags,
                                         l.traversedNodes) )
  }

  def askNode(questionnaireId: String, nodeId: String) = UserSessionAction { req =>
    val flowChartId = req.userSession.engineState.getCurrentChartId
    val nodeId = req.userSession.engineState.getCurrentNodeId
    val askNode = global.Global.interview.getFlowChart(flowChartId).getNode(nodeId).asInstanceOf[AskNode]
    Ok( views.html.interview.question(questionnaireId,
                                      askNode,
                                      req.userSession.tags,
                                      req.userSession.traversed) )
  }

  def answer(questionnaireId: String, nodeId: String) = UserSessionAction { implicit request =>
     val userSession  =request.userSession
     val answerForm = Form( tuple("answerText"->text, "nodeId"->text) )
     answerForm.bindFromRequest().fold (
      failed => Ok("Form submission error %s\n data:%s".format( failed.errors, failed.data)),
      { value  => 
        
        val runRes = advanceEngine( userSession.engineState, value._1 )
        Cache.set( userSession.key, userSession.copy( engineState=runRes.state, 
                                             traversed=userSession.traversed ++ runRes.traversed))
        val status = runRes.state.getStatus
        status match {
          case RuntimeEngineStatus.Running => Redirect( routes.Interview.askNode( questionnaireId, runRes.state.getCurrentNodeId ) )
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

  // TODO: move to some akka actor, s.t. the UI can be reactive
  def advanceEngine( state: RuntimeEngineState, ans: String ) : EngineRunResult = {
    val interview = global.Global.interview
    val  rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    rte.applySnapshot( state )
    rte.consume( new Answer(ans) )

    return EngineRunResult( rte.createSnapshot, l.traversedNodes, l.exception )
        
  }

}
