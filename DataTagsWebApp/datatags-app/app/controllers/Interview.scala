package controllers

import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

import edu.harvard.iq.datatags.runtime.RuntimeEngine

import models._

/**
 * Controller for the interview part of the application.
 */
object Interview extends Controller {

  def interviewIntro(questionnaireId: String) = Action { implicit request =>

    val uuid = session.get("uuid").getOrElse(java.util.UUID.randomUUID().toString)

    val userSession = UserSession( uuid,
      null, questionnaireId, new java.util.Date )

    Cache.set(uuid, userSession)
    val fcs = global.Global.interview
    val dtt = global.Global.dataTags
    Ok( views.html.interview.startPage(fcs,dtt) )
      .withSession( session + ("uuid" -> uuid) )
  }

  def startInterview( questionnaireId:String ) = Action { req =>
    req.session.get("uuid").map { uuid =>

      val interview = global.Global.interview
      val rte = new RuntimeEngine
      rte.setChartSet( interview )
      val l = rte.setListener( new TaggingEngineListener )
      rte.start( interview.getDefaultChartId )



    }.getOrElse{
      Redirect( routes.Interview.interviewIntro(questionnaireId) )
    }
  }

  def askNode(questionnaireId: String, nodeId: String) = Action { req =>
    req.session.get("uuid").map { uuid =>

      Ok(questionnaireId + " " + nodeId)

    }.getOrElse{
      Redirect( routes.Interview.interviewIntro(questionnaireId) )
    }
  }

  def answer(questionnaireId:String, nodeId:String) = Action{ request =>
    Ok( "you said %s".format(request.tags.toString) )
  }

}
