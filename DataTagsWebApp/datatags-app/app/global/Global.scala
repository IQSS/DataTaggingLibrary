package global

import scala.collection.JavaConverters._

import play.api._
import java.nio.file._
import edu.harvard.iq.datatags.runtime._
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.model.types._

object Global extends GlobalSettings {
  
  // Top level type. Instances of this type are the result of the tagging process.
  var dataTags: CompoundType = null
  var interview: FlowChartSet = null;

  override def onStart( app:Application ) {
    Logger.info("DataTags application started")
    val p = Paths.get("public/interview")
    Logger.info( "Loading interview data from " + p.toAbsolutePath.toString )

    val dp = new DataDefinitionParser()
    dataTags = dp.parseTagDefinitions( readAll(p.resolve("definitions.dtDef")), "definitions").asInstanceOf[CompoundType]
    val fcsParser = new FlowChartSetComplier( dataTags )

    val source = readAll( p.resolve("questionnaire.dtf") )

    interview = fcsParser.parse(source, "Data Deposit Screening" )
	  Logger.info("Default chart id: %s".format(interview.getDefaultChartId) )
  }
  
  def readAll( p:Path ) : String = scala.io.Source.fromFile( p.toFile ).getLines().mkString("\n")
  
}
