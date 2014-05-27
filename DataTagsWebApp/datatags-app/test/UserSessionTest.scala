
import org.scalatestplus.play._
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import models._

class UserSessionSpeck extends PlaySpec {

"A New UserSession" must {
  "Have an empty traversed node history" in {
    val sut = UserSession.create("sample")
    sut.traversed.size mustBe 0
    sut.answerHistory.size mustBe 0
  }
}

"A UserSession" must {
  "be updated when using updatedWith" in {
    val base = UserSession.create("sample")
    val ans1 = AnswerRecord( new AskNode("a","AA"), new Answer("indeed") )
    val history1 = Seq( new TodoNode("a"), new TodoNode("b"), new TodoNode("c") )

    val updated = base.updatedWith( ans1, history1, null )

    updated.traversed mustEqual history1
    updated.answerHistory mustEqual Seq(ans1)
    
    val ans2 = AnswerRecord( new AskNode("b","BB"), new Answer("indeed") )
    val history2 = Seq( new TodoNode("B"), new TodoNode("BB"), new TodoNode("BBB") )

    val updated2 = updated.updatedWith( ans2, history2, null )

    updated2.traversed mustEqual history1++history2
    updated2.answerHistory mustEqual Seq(ans1, ans2)
  }
}

}