package controllers

import play.api.mvc._
import play.api._
import play.api.cache.Cache
import play.api.Play.current

import edu.harvard.iq.datatags.runtime._
import edu.harvard.iq.datatags.model.charts.nodes.AskNode
import edu.harvard.iq.datatags.model.values._

import models._
import play.api.data._
import play.api.data.Forms._

/**
 * Controller for the interview part of the application.
 */
object Interview extends Controller {

  def interviewIntro(questionnaireId: String) = Action { implicit request =>

    val uuid = session.get("uuid").getOrElse(java.util.UUID.randomUUID().toString)

    val userSession = new UserSession( uuid, questionnaireId )

    Cache.set(uuid, userSession)
    val fcs = global.Global.interview
    val dtt = global.Global.dataTags
    Ok( views.html.interview.intro(fcs,dtt) )
      .withSession( session + ("uuid" -> uuid) )
  }

  def startInterview( questionnaireId:String ) = Action { req =>
    req.session.get("uuid").map { uuid =>
      Cache.getAs[UserSession](uuid).map{ userSession =>
        val interview = global.Global.interview
        val  rte = new RuntimeEngine
        rte.setChartSet( interview )
        val l = rte.setListener( new TaggingEngineListener )
        rte.start( interview.getDefaultChartId )
        val userSession = Cache.getAs[UserSession](uuid).get
        val updated = userSession.copy( engineState = rte.createSnapshot(),
                                          traversed = l.traversedNodes )
        Cache.set(uuid, updated)

        Logger.info( "session: " + userSession.toString )
        Logger.info( "updated: " + updated.toString )

        Ok( views.html.interview.question(questionnaireId, rte.getCurrentNode.asInstanceOf[AskNode], l.traversedNodes) )
      }.getOrElse {
        Logger.warn("Huh?! has uuid of %s but no session".format(uuid) )
        Redirect( routes.Interview.interviewIntro(questionnaireId) )
      }
    }.getOrElse{
      Redirect( routes.Interview.interviewIntro(questionnaireId) )
    }
  }

  def askNode(questionnaireId: String, nodeId: String) = Action { req =>
    req.session.get("uuid").map { uuid =>
      Cache.getAs[UserSession](uuid).map { userSession => 
        
        Ok(questionnaireId + " " + nodeId)

      }.getOrElse {
        Logger.warn("Huh?! has uuid of %s but no session".format(uuid))
        Redirect( routes.Interview.interviewIntro(questionnaireId) )
      }
    }.getOrElse{
      Redirect( routes.Interview.interviewIntro(questionnaireId) )
    }
  }

  def answer(questionnaireId: String, nodeId: String) = Action{ implicit request =>
    request.session.get("uuid").map { uuid =>
      Cache.getAs[UserSession](uuid).map{ userSession =>
         val answerForm = Form( tuple("answerText"->text, "nodeId"->text) )
         answerForm.bindFromRequest().fold (
          failed => Ok("Form submission error %s\n data:%s".format( failed.errors, failed.data)),
          { value  => 
            
            Logger.info( "answer: %s".format(value._1) )
            Logger.info( "engineState: %s".format(userSession.engineState) )
            Logger.info( "engineState.status: %s".format(userSession.engineState.getStatus) )

            val runRes = advanceEngine( userSession.engineState, value._1 )
            Cache.set( uuid, userSession.copy( engineState=runRes.state, 
                                                 traversed=userSession.traversed ++ runRes.traversed))
            val status = runRes.state.getStatus
            // Ok( status.toString )
            status match {
              case RuntimeEngineStatus.Running => Redirect( routes.Interview.askNode( questionnaireId, runRes.state.getCurrentNodeId ) )
              case RuntimeEngineStatus.Reject  => Redirect( routes.Interview.reject( questionnaireId ) )
              case RuntimeEngineStatus.Accept  => Redirect( routes.Interview.accept( questionnaireId ) )
              case _ => InternalServerError("Bad interview state")
            }
          }
         )

      }.getOrElse {
        Logger.warn("Huh?! has uuid of %s but no session".format(uuid) )
        Redirect( routes.Interview.interviewIntro(questionnaireId) )
      }
    }.getOrElse{
      Logger.warn("User does not have a uuid" )
      Redirect( routes.Interview.interviewIntro(questionnaireId) )
    }
  }

  def accept( questionnaireId:String ) = Action {
    Ok( "%s accepted".format(questionnaireId) )
  }
  def reject( questionnaireId:String ) = Action {
    Ok( "%s Rejected".format(questionnaireId) )
  }

  // TODO: move to some akka actor, s.t. the UI can be reactive
  def advanceEngine( state: RuntimeEngineState, ans: String ) : EngineRunResult = {
    val interview = global.Global.interview
    val  rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    Logger.info("State=" + state)
    Logger.info("State.status=" + state.getStatus)
    rte.applySnapshot( state )
    rte.consume( new Answer(ans) )

    return EngineRunResult( rte.createSnapshot, l.traversedNodes, l.exception )
        
  }

}
