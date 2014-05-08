package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
	val tags = global.Global.dataTags.toString
    Ok(views.html.main( tags ))
  }

}
