package controllers

import play.api.mvc._
import scala.collection.JavaConverters._

object Application extends Controller {

  def index = Action {
	  val tags = global.Global.dataTags.toString
    val itv = global.Global.interview

    Ok(views.html.main( tags, Seq((itv.getDefaultChartId, itv.getDefaultChartId))) )
  }

}
