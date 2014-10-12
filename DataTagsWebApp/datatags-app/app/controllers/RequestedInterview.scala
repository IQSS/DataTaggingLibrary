package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Json
import edu.harvard.iq.datatags.model.types._
import edu.harvard.iq.datatags.model.values._

import models._
import _root_.util.Jsonizer

import play.api.libs.concurrent.Execution.Implicits.defaultContext



object RequestedInterview extends Controller {

  def start(uniqueLinkId: String) = Action { implicit request =>

    Cache.getAs[RequestedInterviewSession](uniqueLinkId) match {

   	  case Some (requestedInterview) => {
        val userSession = UserSession.create(uniqueLinkId)
        val userSessionWithInterview = userSession.updatedWithRequestedInterview(requestedInterview)

        Cache.set(userSessionWithInterview.key, userSessionWithInterview)

        val fcs = QuestionnaireKits.kit.questionnaire
        val dtt = QuestionnaireKits.kit.tags
        val message = Option("Welcome, Dataverse user! Please follow the directions below to begin tagging your data.")

        Ok( views.html.interview.intro(fcs,dtt, message) )
          .withSession( request2session + ("uuid" -> userSessionWithInterview.key) )
        }

   	  case None => BadRequest
   }

  }



  def postBackTo(uniqueLinkId: String) = UserSessionAction.async { implicit request =>
      val json = request.userSession.tags.accept(Jsonizer)
      val callbackURL = request.userSession.requestedInterview.get.callbackURL
      Logger.info(callbackURL)
      WS.url(callbackURL).post(json).map { response =>
        Redirect((response.json \ "data" \ "message").as[String])
      }
  }




  def unacceptableDataset(uniqueLinkId: String, reason: String) = UserSessionAction.async { implicit request =>
      val callbackURL = request.userSession.requestedInterview.get.callbackURL

      WS.url(callbackURL).post(Json.toJson(reason)).map { response =>
        Redirect((response.json \ "data" \ "message").as[String])
      }
  }

}
