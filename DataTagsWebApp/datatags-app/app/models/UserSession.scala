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
  sessionStart: Date,
  requestedInterview: Option[RequestedInterviewSession] ) {

  def tags = {
    val parser = new edu.harvard.iq.datatags.io.StringMapFormat
    val tagType = QuestionnaireKits.kit.tags
    parser.parse( tagType, engineState.getSerializedTagValue ).asInstanceOf[CompoundValue]
  }

  def updatedWith( ansRec: AnswerRecord, newNodes: Seq[Node], state: RuntimeEngineState ) = 
        copy( engineState=state, answerHistory=answerHistory :+ ansRec, traversed=traversed++newNodes)

  def updatedWith( newNodes: Seq[Node], state: RuntimeEngineState ) = 
        copy( engineState=state, traversed=traversed++newNodes)  

  def replaceHistory( answers: Seq[AnswerRecord], history:Seq[Node], state: RuntimeEngineState ) = 
        copy( engineState=state, traversed=history, answerHistory=answers )

  def updatedWithRequestedInterview( requestedUserInterview: RequestedInterviewSession) =
        copy (requestedInterview = Option(requestedUserInterview))

}

object UserSession {
  def create( questionnaireId: String ) = 
        UserSession( java.util.UUID.randomUUID().toString, 
                     null,
                     Seq(),
                     questionnaireId, 
                     Seq(), 
                     new Date,
                     None )
}
