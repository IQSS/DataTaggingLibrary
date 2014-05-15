package models

import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import java.util.Date

/**
 * All the data needed to maintain continuous user experience.
 * TODO: Add a Seq of (question-id, answer) pairs
 */
case class UserSession(
  key:String,
  engineState: RuntimeEngineState,
  traversed: Seq[Node],
  questionnaireId: String,
  sessionStart: Date ) {

  def this( key:String,
            questionnaireId: String ) = 
      this(key, null, Seq[Node](), questionnaireId, new java.util.Date() )
}
