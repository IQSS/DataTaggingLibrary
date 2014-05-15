package models

import java.util.Date
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.values.CompoundValue
import edu.harvard.iq.datatags.model.charts.nodes._

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

  def tags = {
    val parser = new edu.harvard.iq.datatags.io.StringMapFormat
    val tagType = global.Global.dataTags
    parser.parse( tagType, engineState.getSerializedTagValue ).asInstanceOf[CompoundValue]
  }
}
