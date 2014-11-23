package models

import play.api._
import java.nio.file._
import edu.harvard.iq.datatags.runtime._
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException
import edu.harvard.iq.datatags.parser.flowcharts._
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.model.values._
import edu.harvard.iq.datatags.model.types._
import views._

case class QuestionnaireKit( val id:String,
                             val title: String,
                             val tags: CompoundType,
                             val questionnaire: FlowChartSet ) {
  val serializer = Serialization( questionnaire, tags )
}

object QuestionnaireKits {
  val allKits = loadQuestionnaires()
  
  /** This will go away once we have multi questionnaire support */
  val kit = allKits.toSeq(0)._2

  private def loadQuestionnaires() = {
    Logger.info("Loading questionnaires")
    Play.current.configuration.getString("datatags.folder") match {
    case Some(str) => {
          val p = Paths.get(str)
          Logger.info( "Loading questionnaire data from " + p.toAbsolutePath.toString )
          
          val definitions = p.resolve("definitions.tags")
          Logger.info( "Reading definitions from %s".format(definitions.toAbsolutePath.toString))
          val dp = new DataDefinitionParser()
          val dataTags = dp.parseTagDefinitions( readAll(definitions), "definitions").asInstanceOf[CompoundType]
          Logger.info( " - DONE")

          val fcsParser = new FlowChartSetComplier( dataTags )

          val questionnaire = p.resolve("questionnaire.flow")
          Logger.info( "Reading questionnaire from %s".format(questionnaire.toAbsolutePath.toString))
          val source = readAll( questionnaire )
          Logger.info( " - READ DONE")
          val interview = fcsParser.parse(source, "Data Deposit Screening" )
          Logger.info( " - PARSING DONE")

          Logger.info("Default chart id: %s".format(interview.getDefaultChartId) )

          Map( "dds-c1" -> QuestionnaireKit("dds-c1", "Data Deposit Screening", dataTags, interview) )
        }

        case None => {
          Logger.error("Bad configuration: Can't find \"datatags.folder\"")
          Map[String, QuestionnaireKit]()
        }
    }
  }

  private def readAll( p:Path ) : String = scala.io.Source.fromFile( p.toFile, "utf-8" ).getLines().mkString("\n")


  private def matchNode(aNode: nodes.Node, answerFrequencies: scala.collection.mutable.Map[Answer, Integer]): scala.collection.mutable.Map[Answer, Integer] = aNode match {

    case n:nodes.AskNode => { // if node is AskNode, update frequency of answer
      val answerItr = n.getAnswers.iterator
      while (answerItr.hasNext) { // while answers remain in the node
        val nextAns = answerItr.next
        if (answerFrequencies.contains(nextAns)) { // if answer is already listed, update number
          val frequency = answerFrequencies(nextAns)
          answerFrequencies.update(nextAns, frequency+1)
        } else { // if not, insert answer
          answerFrequencies.put(nextAns,1)
        }
      }
      answerFrequencies
     }

    case _ => { // if node is any other node, simply return the current answer frequency map
      answerFrequencies
    }
  }

}


