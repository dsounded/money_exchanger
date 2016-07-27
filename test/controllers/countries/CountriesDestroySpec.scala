import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

import utils.TimeUtil.now

class CountriesDestroySpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Countries"))
    DatabaseInserter.insert("Users", 12, List("john-doe_index@gmail.com", "John", "Doe", "password", "token", now.toString, "User", "1", "999999", "2016-01-01"))
    DatabaseInserter.insert("Countries", 11, List("Denmark", "DEN", "1", "2016-10-10"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Countries"))
  }

  "deleting countries" should {
    "returns 404 if no such record" in {
      val request = FakeRequest(DELETE, "/countries/200").withHeaders("Authorization" -> "token")
      val destroy = route(app, request).get

      status(destroy) mustBe NOT_FOUND
    }

    "returns 204 if successfully deleted" in {
      val request = FakeRequest(DELETE, "/countries/11").withHeaders("Authorization" -> "token")
      val destroy = route(app, request).get

      status(destroy) mustBe NO_CONTENT
    }
  }
}
