package controllers
import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.cache.Cache
import play.api.Play.current

import models._

/**
A request with a userSession. If non exists, it goes to a default page
*/
class UserSessionRequest[A](val userSession: UserSession, request: Request[A]) extends WrappedRequest[A](request)

object UserSessionAction extends ActionBuilder[UserSessionRequest] {

  def invokeBlock[A](request: Request[A], block: (UserSessionRequest[A]) => Future[Result]) = {
    request.session.get("uuid").map { uuid =>
      Cache.getAs[UserSession](uuid).map{ userSession =>
        block(new UserSessionRequest(userSession, request))
      }.getOrElse {
        Logger.warn("Huh?! has uuid of %s but no session".format(uuid) )
        Future.successful( Redirect( routes.Application.index) )
      }
    }.getOrElse{
      Logger.warn("User does not have a uuid" )
      Future.successful( Redirect( routes.Application.index) )
    }

  }
}
