import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

class CountriesCreateSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Countries"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Countries"))
  }

  "saves the record on create" in {
    val request = FakeRequest(POST, "/countries").withJsonBody(Json.parse("""{ "country": {"title":"Germany", "abbreviation":"GER"} }"""))
    val create = route(app, request).get

    status(create) mustBe CREATED
    contentType(create) mustBe Some("application/json")
    contentAsString(create) must include("country")
  }
}
