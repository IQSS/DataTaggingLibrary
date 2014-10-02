
import org.scalatestplus.play._
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import models._
import views._

class AnswerSerializationTest extends PlaySpec {

// one yes answer must be 0
"A yes answer" must {
	"be serialized to a \"0\"" in {
		val answerDictionary = Map("0" -> "yes", "1" -> "no")
		val ans1 = new Answer("yes")
		scala.Console.println(Serialization.encodeClientAnswers("", ans1, answerDictionary))
		Serialization.encodeClientAnswers("", ans1, answerDictionary) mustEqual "0"
	}
}

// one no answer must be 1
"A no answer" must {
	"be serialized to a \"1\"" in {
		val answerDictionary = Map("0" -> "yes", "1" -> "no")
		val ans2 = new Answer("no")
		Serialization.encodeClientAnswers("", ans2, answerDictionary) mustEqual "1"
	}
}

// one 1 must be yes
"A \"0\"" must {
	"be deserialized to a yes answer" in {
		// Initializes the UserSession
		// Normally, the UserSession history would be corrupt: here it actually matches the end output
		val userSession = UserSession.create("sample")
		val aNode = new AskNode("a", "AA") // first AskNode is always the same
		val ansSeq: Seq[AnswerRecord] = Seq(AnswerRecord( aNode, new Answer("yes")))
		val nodeHistory = Seq( aNode )
		val userSessionUpdated = userSession.replaceHistory(ansSeq, nodeHistory, null)

		val answerDictionary = Map("0" -> "yes", "1" -> "no")
		val serializedAnswers = "0"

		Serialization.decodeClientAnswers(serializedAnswers, answerDictionary, userSessionUpdated) mustBe userSessionUpdated
	}
}

// one 0 must be no
"A \"1\"" must {
	"be deserialized to a no answer" in {
		// Initializes the UserSession
		// Normally, the UserSession history would be corrupt: here it actually matches the end output
		val userSession = UserSession.create("sample")
		val aNode = new AskNode("a", "AA") // first AskNode is always the same
		val ansSeq: Seq[AnswerRecord] = Seq(AnswerRecord( aNode, new Answer("no")))
		val nodeHistory = Seq( aNode )
		val userSessionUpdated = userSession.replaceHistory(ansSeq, nodeHistory, null)

		val answerDictionary = Map("0" -> "yes", "1" -> "no")
		val serializedAnswers = "1"

		Serialization.decodeClientAnswers(serializedAnswers, answerDictionary, userSessionUpdated) mustBe userSessionUpdated
	}
}

// a sequence of 10101 must be no yes no yes no
"A \"10101\"" must {
	"be deserialized to an answer sequence of no yes no yes no" in {
		// Initializes the UserSession
		// Normally, the UserSession history would be corrupt: here it actually matches the end output
		val userSession = UserSession.create("sample")

		val aNode = new AskNode("a", "AA") // first AskNode is always the same
		val bNode = new AskNode("b", "BB")
		val cNode = new AskNode("c", "CC")
		val dNode = new AskNode("d", "DD")
		val eNode = new AskNode("e", "EE")

		aNode.setNodeFor(new Answer("no"), bNode)
		bNode.setNodeFor(new Answer("yes"), cNode)
		cNode.setNodeFor(new Answer("no"), dNode)
		dNode.setNodeFor(new Answer("yes"), eNode)

		val ansSeq = Seq( AnswerRecord( aNode, new Answer("no")),
						  AnswerRecord( bNode, new Answer("yes")),
						  AnswerRecord( cNode, new Answer("no")),
						  AnswerRecord( dNode, new Answer("yes")),
						  AnswerRecord( eNode, new Answer("no")))
		val nodeHistory = Seq( aNode, bNode, cNode, dNode, eNode )
		val userSessionUpdated = userSession.replaceHistory(ansSeq, nodeHistory, null)

		val answerDictionary = Map("0" -> "yes", "1" -> "no")
		val serializedAnswers = "10101"

		Serialization.decodeClientAnswers(serializedAnswers, answerDictionary, userSessionUpdated) mustBe userSessionUpdated
	}
}

// a sequence of 01010 must be no yes no yes no
"A \"01010\"" must {
	"be deserialized to an answer sequence of yes no yes no yes" in {
		// Initializes the UserSession
		// Normally, the UserSession history would be corrupt: here it actually matches the end output
		val userSession = UserSession.create("sample")

		val aNode = new AskNode("a", "AA") // first AskNode is always the same
		val bNode = new AskNode("b", "BB")
		val cNode = new AskNode("c", "CC")
		val dNode = new AskNode("d", "DD")
		val eNode = new AskNode("e", "EE")

		aNode.setNodeFor(new Answer("yes"), bNode)
		bNode.setNodeFor(new Answer("no"), cNode)
		cNode.setNodeFor(new Answer("yes"), dNode)
		dNode.setNodeFor(new Answer("no"), eNode)

		val ansSeq = Seq( AnswerRecord( aNode, new Answer("yes")),
						  AnswerRecord( bNode, new Answer("no")),
						  AnswerRecord( cNode, new Answer("yes")),
						  AnswerRecord( dNode, new Answer("no")),
						  AnswerRecord( eNode, new Answer("yes")))
		val nodeHistory = Seq( aNode, bNode, cNode, dNode, eNode )
		val userSessionUpdated = userSession.replaceHistory(ansSeq, nodeHistory, null)

		val answerDictionary = Map("0" -> "yes", "1" -> "no")
		val serializedAnswers = "01010"

		Serialization.decodeClientAnswers(serializedAnswers, answerDictionary, userSessionUpdated) mustBe userSessionUpdated
	}
}

}