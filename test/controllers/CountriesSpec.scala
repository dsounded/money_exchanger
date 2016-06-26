import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

class CountriesSpec extends PlaySpec with OneAppPerTest {
  "Countries" should {
    "render the index page" in {
      val index = route(app, FakeRequest(GET, "/countries")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("application/json")
      contentAsString(index) must include("countries")
    }

    "saves the record on create" in {
      val request = FakeRequest(POST, "/countries").withJsonBody(Json.parse("""{ "country": {"title":"Germany", "abbreviation":"GER"} }"""))
      val create = route(app, request).get

      status(create) mustBe OK
      contentType(create) mustBe Some("application/json")
      contentAsString(create) must include("country")
    }
  }
}
