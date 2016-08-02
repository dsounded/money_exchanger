import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

import utils.TimeUtil.now

class UsersIndexSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Users"))
    DatabaseInserter.insert("Users", 12, List("john-doe_index@gmail.com", "John", "Doe", "password", "token", now.toString, "User", "1", "999999", "2016-01-01"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Users"))
  }

  "renders the index response" in {
    val index = route(app, FakeRequest(GET, "/users").withHeaders("Authorization" -> "token")).get

    status(index) mustBe OK
    contentType(index) mustBe Some("application/json")
    contentAsString(index) must include("users")
    contentAsString(index) mustNot include("authToken")
  }
}
