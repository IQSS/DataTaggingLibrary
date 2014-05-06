package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

object Test extends Controller {
  
//  def nameCount( name:String, count:Int ) = Action {
//	Ok( views.html.nameCount( "Hello " + name, count) )
//  }
  
  def nameCount( name:String, count:Int ) = Action.async {
	val futureString = scala.concurrent.Future { (name+" ")*count }
	futureString.map( s => Ok(views.html.nameCount( "Hello " + s, count)) )
  }
}
