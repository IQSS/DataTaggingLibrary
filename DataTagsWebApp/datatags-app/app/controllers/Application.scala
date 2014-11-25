package controllers

import play.api.mvc._
import play.api.Routes
import play.api.Play.current
import play.api.cache.Cached
import models._

object Application extends Controller {

  def index = Cached("homePage"){
    Action { implicit request =>
    Ok(
      views.html.index(TagsTable.rows,
                        routes.Interview.interviewIntro(QuestionnaireKits.kit.id) ))
    }
  }

  def questionnaireCatalog = Action {
    Ok( views.html.questionnaireCatalog(QuestionnaireKits.allKits.toSeq.map( p=>(p._2.title, p._1))) )
  }

  def changeLog = Action {
    Ok( views.html.changeLog() )
  }

  def javascriptRoutes = Cached("jsRoutes") {
    Action { implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          routes.javascript.Interview.askNode,
          routes.javascript.Interview.answer,
          routes.javascript.Interview.interviewIntro,
          routes.javascript.Interview.startInterview
        )
      ).as("text/javascript")
    }
  }

}
