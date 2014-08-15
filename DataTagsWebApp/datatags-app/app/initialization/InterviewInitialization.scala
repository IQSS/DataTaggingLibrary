package initialization

import scala.collection.JavaConverters._

import play.api._
import java.nio.file._
import edu.harvard.iq.datatags.runtime._
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException
import edu.harvard.iq.datatags.parser.flowcharts._
// import edu.harvard.iq.datatags.tools.RepeatIdValidator
// import edu.harvard.iq.datatags.tools.UnreachableNodeValidator
// import edu.harvard.iq.datatags.tools.ValidCallNodeValidator
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.model.types._

trait Initialization {
	def onStart
	def readAll( p:Path ) : String = scala.io.Source.fromFile( p.toFile ).getLines().mkString("\n")
}

object InterviewInitialization extends Initialization {

	var dataTags: CompoundType = null
	var interview: FlowChartSet = null

	def onStart {
		Logger.info("DataTags application started")
	    Play.current.configuration.getString("datatags.folder") match {
			case Some(str) => {
        		val p = Paths.get(str)
        		Logger.info( "Loading interview data from " + p.toAbsolutePath.toString )

        		val dp = new DataDefinitionParser()
        		dataTags = dp.parseTagDefinitions( readAll(p.resolve("definitions.tags")), "definitions").asInstanceOf[CompoundType]
        		val fcsParser = new FlowChartSetComplier( dataTags )

        		val source = readAll( p.resolve("questionnaire.flow") )

        		interview = fcsParser.parse(source, "Data Deposit Screening" )
        		Logger.info("Default chart id: %s".format(interview.getDefaultChartId) )

        		
        		// TODO validation of nodes
        	// 	var validationMessages: scala.collection.mutable.LinkedList[ValidationMessages] = new scala.collection.mutable.LinkedList()
        	// 	val instructionNodes = new FlowChartASTParser().graphParser().parse(source)

        	// 	val repeatValidator = new RepeatIdValidator();
        	// 	validationMessages ++ repeatValidator.validateRepeatIds(instructionNodes)

        	// 	val callNodeValidator = new ValidCallNodeValidator();
        	// 	validationMessages ++ callNodeValidator.validateIdReferences(interview)

        	// if (validationMessages.size != 0) {
        	// 	throw new ValidationException
        	// }

        	// 	val unreachableValidator = new UnreachableNodeValidator();
        	// 	validationMessages ++ unreachableValidator.validateUnreachableNodes(interview)

    	  	//  Logger.info("Validation Messages: " + validationMessages )
      		}
      		case None => Logger.error("Bad configuration: Can't find \"datatags.folder\"")
    	}
	}

	def getDataTags: CompoundType = dataTags

	def getInterview: FlowChartSet = interview

}