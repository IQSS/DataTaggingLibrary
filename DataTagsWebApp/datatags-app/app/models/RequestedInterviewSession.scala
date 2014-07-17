package models

import java.util.Date
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._


/**
 * Holds the callback URL and the repository name for later use
 */
case class RequestedInterviewSession(
  callbackURL: String,
  repositoryName: String ) {

  val key = java.util.UUID.randomUUID().toString
  val sessionStart = new Date

}
