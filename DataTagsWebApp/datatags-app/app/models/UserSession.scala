package models

import java.util.Date
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._

case class AnswerRecord( question: AskNode, answer: Answer)

/**
 * All the data needed to maintain continuous user experience.
 */
case class UserSession(
  key:String,
  engineState: RuntimeEngineState,
  traversed: Seq[Node],
  questionnaireId: String,
  answerHistory: Seq[AnswerRecord],
  sessionStart: Date ) {

  def tags = {
    val parser = new edu.harvard.iq.datatags.io.StringMapFormat
    val tagType = global.Global.dataTags
    parser.parse( tagType, engineState.getSerializedTagValue ).asInstanceOf[CompoundValue]
  }

  def updatedWith( ansRec: AnswerRecord, newNodes: Seq[Node], state: RuntimeEngineState ) = 
        copy( engineState=state, answerHistory=answerHistory :+ ansRec, traversed=traversed++newNodes)  
}

object UserSession {
  def create( questionnaireId: String ) = 
        UserSession( java.util.UUID.randomUUID().toString, 
                     null,
                     Seq(),
                     questionnaireId, 
                     Seq(), 
                     new Date )
}
