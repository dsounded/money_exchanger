import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

class CountriesIndexSpec extends PlaySpec with BeforeAndAfterAll {
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

  "render the index response" in {
    val index = route(app, FakeRequest(GET, "/countries")).get

    status(index) mustBe OK
    contentType(index) mustBe Some("application/json")
    contentAsString(index) must include("countries")
  }
}
