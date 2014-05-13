package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
	  val tags = global.Global.dataTags.toString
    val itv = global.Global.interview

    Ok(views.html.main( Seq((itv.getDefaultChartId, itv.getDefaultChartId))) )
  }

}
