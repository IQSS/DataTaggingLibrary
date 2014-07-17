package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.cache.Cache
import play.api.Play.current
import models._
/** Uncomment the following lines as needed **/
/**
import play.api.Play.current
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import java.util.concurrent._
import scala.concurrent.stm._
import akka.util.duration._
import play.api.cache._
import play.api.libs.json._
**/

object RequestedInterview extends Controller {

  def start(uniqueLinkId: String) = Action { implicit request =>

    Cache.getAs[RequestedInterviewSession](uniqueLinkId) match {
   	  case Some (requestedInterview) => {
        val userSession = UserSession.create(uniqueLinkId)
        val userSessionWithInterview = userSession.updatedWithRequestedInterview(requestedInterview)

        Cache.set(userSessionWithInterview.key, userSessionWithInterview)

        val fcs = global.Global.interview
        val dtt = global.Global.dataTags

        Ok( views.html.interview.intro(fcs,dtt) )
          .withSession( session + ("uuid" -> userSessionWithInterview.key) ) }

   	  case None => BadRequest
   }

  }

}