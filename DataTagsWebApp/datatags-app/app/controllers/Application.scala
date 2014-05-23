package controllers

import play.api.mvc._
import models._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok( views.html.index(TagsTable.rows) )
  }

  def questionnaireCatalog = Action {
	  val tags = global.Global.dataTags.toString
    val itv = global.Global.interview

    Ok(views.html.questionnaireCatalog( Seq((itv.getDefaultChartId, itv.getDefaultChartId))) )
  }

  def changeLog = Action {
    Ok( views.html.changeLog() )
  }
}
