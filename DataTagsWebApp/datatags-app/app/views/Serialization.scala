package views

import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import edu.harvard.iq.datatags.model.types.CompoundType
import edu.harvard.iq.datatags.runtime._
import edu.harvard.iq.datatags.model.charts._
import models._
import scala.collection.JavaConversions._


/*** 
 * Class to deal with de/serialization of UserSession answer history
 */
class Serialization private( val answerMap: Map[Answer, String],
                             val serializedMap: Map[String, Answer],
                             val questionnaire: FlowChartSet, 
                             val tagsType: CompoundType ) {

  /**
   * Take the current AnswerRecords and return the serialized version
   */
  def encode(answerRecords: Seq[AnswerRecord]) = 
              answerRecords.map( _.answer ).map( answerMap ).mkString

  /**
   * Take the serialized answers and a UserSession, and return
   * a UserSession with the history replaced by the run encoded {@param serializedAns}.
   */
  def decode(serializedAns: String, userSession: UserSession) : UserSession = {
    // Setup runtime environment
    val rte = new RuntimeEngine
    val l = rte.setListener( new TaggingEngineListener )
    val buffer = collection.mutable.Buffer[AnswerRecord]()
    rte.setChartSet( questionnaire )
    rte.setCurrentTags( tagsType.createInstance )

    // Deserialize and feed the answers to rte
    rte.start( questionnaire.getDefaultChartId )
    serializedAns.map(_.toString).map( serializedMap ).foreach(ans => { 
        buffer append AnswerRecord(rte.getCurrentNode.asInstanceOf[AskNode], ans)
        rte.consume( ans )
    })
    userSession.replaceHistory(buffer.toList, l.traversedNodes, rte.createSnapshot)
   }

}

object Serialization {
  def apply( questionnaire:FlowChartSet, tagsType:CompoundType ):Serialization = {
    // first - get the answer frequencies
    val answers = getAnswersSortedByFrequencies(questionnaire)
    if ( answers.size > '~'-'!'+1 ) {
      throw new IllegalArgumentException(s"Serialization currently does not support ${answers.size} answers")
    }
    // now make the map and create the serialization.
    val ans2char = answers.zipWithIndex.map( p=>(p._1, ('!'+p._2).toChar.toString) ).toMap

    new Serialization( ans2char, ans2char.map( e => (e._2, e._1)), questionnaire, tagsType )
  }

  def getAnswersSortedByFrequencies( questionnaire: FlowChartSet ) : Seq[Answer] = {
    val answerList = questionnaire.charts.flatMap( chart => chart.nodes )
      .flatMap( n => n match {
        case a:AskNode => a.getAnswers.toList
        case _ => Nil
        })
    answerList.groupBy( p=>p ).map( p=>(p._1, p._2.size) ).toList.sortBy( -_._2 ).map( _._1 )
  }

}
