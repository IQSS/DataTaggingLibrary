
import org.scalatestplus.play._
import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.model.values._
import models._

import views.Helpers

class HelperSpec extends PlaySpec {

"All lowercase" must {
  "end up as titlecase" in {
    Helpers.makeUpper("no") mustBe "No"
    Helpers.makeUpper("hello world") mustBe "Hello World"
  }
}

"Lowercase and titlecase" must {
  "end up as titlecase" in {
    Helpers.makeUpper("hello World") mustBe "Hello World"
    Helpers.makeUpper("This should work") mustBe "This Should Work"
  }
}

"Lowercase and proper punctuation" must {
  "end up as titlecase" in {
    Helpers.makeUpper("hello world!") mustBe "Hello World!"
    Helpers.makeUpper("this should work!") mustBe "This Should Work!"
  }
}

"Mixed and all uppercase" must {
  "not change" in {
    Helpers.makeUpper("iRODS") mustBe "iRODS"
    Helpers.makeUpper("HIPAA") mustBe "HIPAA"
  }
}

"Lowercase and numbers" must {
  "end up as titlecase and numbers" in {
    Helpers.makeUpper("50 years") mustBe "50 Years"
    Helpers.makeUpper("the last 20 years") mustBe "The Last 20 Years"
  }
}


}