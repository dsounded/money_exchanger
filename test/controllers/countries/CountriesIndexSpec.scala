import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

import utils.TimeUtil.now

class CountriesIndexSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Countries"))
    DatabaseInserter.insert("Users", 12, List("john-doe_index@gmail.com", "John", "Doe", "password", "token", now.toString, "User", "1", "999999", "2016-01-01"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Countries"))
  }

  "renders the forbidden for unauthorized response" in {
    val request = FakeRequest(GET, "/countries")
    val index = route(app, request).get

    status(index) mustBe FORBIDDEN
    contentType(index) mustBe Some("application/json")
    contentAsString(index) must include("errors")
  }

  "renders countries list for authorized user response" in {
    val request = FakeRequest(GET, "/countries").withHeaders("Authorization" -> "token")
    val index = route(app, request).get

    status(index) mustBe OK
    contentType(index) mustBe Some("application/json")
    contentAsString(index) must include("countries")
  }
}
