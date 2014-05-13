package models

import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import java.util.Date

/**
 * All the data needed to maintain continuous user experience.
 */
case class UserSession(
  key:String,
  engineState: RuntimeEngineState,
  questionnaireId: String,
  sessionStart: Date ) {

}
