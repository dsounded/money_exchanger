import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.DatabaseCleaner

import play.api.Play

class UsersSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.start(app)

    DatabaseCleaner.clean(List("Users"))
  }

  "Users" should {
    "render the index response" in {
      val index = route(app, FakeRequest(GET, "/users")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("application/json")
      contentAsString(index) must include("users")
    }
  }
}
