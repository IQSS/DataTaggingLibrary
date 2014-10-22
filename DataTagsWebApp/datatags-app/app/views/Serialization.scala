package views

import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import edu.harvard.iq.datatags.runtime._
import models._
import controllers._


/*** 
 * Class to deal with de/serialization of UserSession answer history
 */
case class Serialization( val answerMap: Map[Answer, String],
                          val serializedMap: Map[String, Answer]) {

  /* Take the current AnswerRecords and return the serialized version */
  def encodeClientAnswers(answerRecords: Seq[AnswerRecord]) = answerRecords.map( _.answer ).map( answerMap ).mkString


  /* Take the serialized answers and the current UserSession, and return
   * an improved UserSession with the correct history for the current node
   */
  def decodeClientAnswers(serializedAns: String, userSession: UserSession) = {
    // fresh UserSession to begin recreating history
    val interview = QuestionnaireKits.kit.questionnaire
    val rte = new RuntimeEngine
    rte.setChartSet( interview )
    val l = rte.setListener( new TaggingEngineListener )
    rte.start( interview.getDefaultChartId )
    var updated = userSession.replaceHistory(Seq[AnswerRecord](), l.traversedNodes, rte.createSnapshot)

    for (index <- 0 to serializedAns.length-1) { // go through all serialized answers
      for (key <- serializedMap.keys) {
        if (key.equals(serializedAns.charAt(index).toString)) { // match serialized ans to its Answer
          val currentAns = serializedMap(key)
          val ansRec = AnswerRecord( Interview.currentAskNode(updated.engineState), currentAns )
          val runRes = Interview.advanceEngine( updated.engineState, currentAns ) // advance the engine by one answer
          updated = updated.updatedWith(ansRec, runRes.traversed, runRes.state)
        }
      }
    }
    updated
   }

}

object Serialization {
  def create( aMap: Map[Answer, String]) =
        Serialization( aMap, aMap.map( e => (e._2, e._1)))
}
