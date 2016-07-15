import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.DatabaseCleaner

import play.api.Play

class CountriesSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.start(app)

    DatabaseCleaner.clean(List("Countries"))
  }

  "Countries" should {
    "render the index page" in {
      val index = route(app, FakeRequest(GET, "/countries")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("application/json")
      contentAsString(index) must include("countries")
    }

    "saves the record on create" in {
      //DatabaseCleaner.clean(List("Countries"))
      val request = FakeRequest(POST, "/countries").withJsonBody(Json.parse("""{ "country": {"title":"Germany", "abbreviation":"GER"} }"""))
      val create = route(app, request).get

      status(create) mustBe CREATED
      contentType(create) mustBe Some("application/json")
      contentAsString(create) must include("country")
    }

    "deleting countries" should {
      "returns 404 if no such record" in {
        val request = FakeRequest(DELETE, "/countries/200")
        val destroy = route(app, request).get

        status(destroy) mustBe NOT_FOUND
      }

      "returns 204 if successfully deleted" in {
        val request = FakeRequest(DELETE, "/countries/1")
        val destroy = route(app, request).get

        status(destroy) mustBe NO_CONTENT
      }
    }
  }
}
