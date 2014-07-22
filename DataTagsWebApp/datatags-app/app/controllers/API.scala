package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.Play.current
import play.api.libs.json.Json

import models._
import play.api.cache.Cache
import controllers._



/**
 * Controller for API.
 */

object API extends Controller {

	
	def requestInterview(repositoryName: String, callbackURL: String) = Action { implicit request =>
		// prepare for the user, cache callback URL and repository name
		val requestedInterviewSession = RequestedInterviewSession(callbackURL, repositoryName)
		Cache.set(requestedInterviewSession.key, requestedInterviewSession, 10800)

		// reverse routing to decide on interview link
		val interviewLink = routes.RequestedInterview.start(requestedInterviewSession.key).absoluteURL()
		
		// send json response with interview link
		Ok(Json.obj("status" -> "OK", "data" -> interviewLink))
	}

}
