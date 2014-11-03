import org.scalatestplus.play._
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.types._
import edu.harvard.iq.datatags.model.charts._
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import models._

class AnswerSerializationTest extends PlaySpec {

/**
 Chart:
	<code>
	 A -y-> B  -y-> C  -y-> D
	 |n     |n      |m      |
	 An -y->Bn -y-> Cm -y-> +
	</code>
 */
val yes = Answer.Answer("yes")
val no = Answer.Answer("no")
val maybe = Answer.Answer("maybe")

def mockFcs = {
	val chart = new FlowChart("Mock FlowChart")
	val a = chart.add(new AskNode("A","aa"))
	val b = chart.add(new AskNode("B","bb"))
	val c = chart.add(new AskNode("C","cc"))
	val d = chart.add(new AskNode("D","dd"))
	val an = chart.add(new AskNode("An","an"))
	val bn = chart.add(new AskNode("Bn","bn"))
	val cm = chart.add(new AskNode("Cm","cm"))
	a.setNodeFor( yes, b )
	b.setNodeFor( yes, c )
	c.setNodeFor( yes, d )

	an.setNodeFor( yes, bn )
	bn.setNodeFor( yes, cm )
	cm.setNodeFor( yes, d  )

	a.setNodeFor( no, an )
	b.setNodeFor( no, bn )
	c.setNodeFor( maybe, cm )

	chart.setStart( a )

	val retVal = new FlowChartSet("mock FCS")
	retVal.addChart( chart )
	retVal.setDefaultChartId( chart.getId )

	retVal
}
val tagsType = new CompoundType("Mock CompoundType", "")
val sut = Serialization( mockFcs, tagsType )

"the mock chart" must {
	"have a yes-no-maybe serialized ans" in {
		val actual = Serialization.getAnswersSortedByFrequencies( mockFcs )
		actual mustEqual Seq( Answer.Answer("yes"), Answer.Answer("no"), Answer.Answer("maybe") )
	}
}


// one yes answer must be 0
"A yes answer" must {
	"be serialized to the Serialization.chars(0) char" in {
		sut.encode( Seq(AnswerRecord(null, Answer.YES))) mustEqual Serialization.chars(0).toString
	}
}

"All round-trips must succeed" must {
	"simple" in {
		println("Simple Roundtrip")
		val fcs = mockFcs
		val chart = fcs.getFlowChart("Mock FlowChart")
		val a = chart.getNode("A").asInstanceOf[AskNode]
		val b = chart.getNode("B").asInstanceOf[AskNode]
		val c = chart.getNode("C").asInstanceOf[AskNode]
		val d = chart.getNode("D").asInstanceOf[AskNode]

		val originalAnswers = Seq( AnswerRecord(a, yes),
															 AnswerRecord(b, yes),
															 AnswerRecord(c, yes) )

		val serialized = sut.encode( originalAnswers )
		val session = UserSession(
									  "mock session",
									  null,
									  Seq(a,b,c),
									  fcs.getId,
									  originalAnswers,
									  new java.util.Date,
									  Option(null) )
		val actual = sut.decode( serialized, session )

		// Nodes don't have a proper equals method at this point.
		// So we'll map them to their ids, and then make the comparisons
		val ar2str = (a:AnswerRecord) => a.question.getId + "-" + a.answer.getAnswerText + "->"

		actual.answerHistory.map( ar2str ) mustEqual originalAnswers.map( ar2str ) 
		actual.traversed.map( _.getId ) mustEqual Seq(a,b,c,d).map( _.getId )
		actual.engineState.getCurrentNodeId mustEqual "D"
	}
}

}