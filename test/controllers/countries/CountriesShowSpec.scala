import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

class CountriesShowSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Countries"))

    DatabaseInserter.insert("Countries", 88, List("Austria", "AU", "0", "2015-01-01"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Countries"))
  }

  "renders country" in {
    val request = FakeRequest(GET, "/countries/88")
    val show = route(app, request).get

    status(show) mustBe OK
    contentType(show) mustBe Some("application/json")
    contentAsString(show) must include("country")
  }

  "renders error" in {
    val request = FakeRequest(GET, "/countries/20")
    val show = route(app, request).get

    status(show) mustBe NOT_FOUND
    contentType(show) mustBe Some("application/json")
    contentAsString(show) must include("error")
    contentAsString(show) must include("not found")
  }
}
