import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.DatabaseCleaner

import play.api.Play

class UsersCreateSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Users"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Users"))
  }

  "creating" should {
    "returns 400 if bad request" in {
      val request = FakeRequest(POST, "/users").withJsonBody(Json.parse("""{ "user": {"password":"pass"} }"""))
      val create = route(app, request).get

      status(create) mustBe BAD_REQUEST
      contentType(create) mustBe Some("application/json")
      contentAsString(create) must include("errors")
      contentAsString(create) must include("\"email\":[\"must not be empty\"]")
    }

    "returns 201 if request is acceptable" in {
      DatabaseCleaner.clean(List("Users"))

      val request = FakeRequest(POST, "/users").withHeaders("Authorization" -> "token").withJsonBody(Json.parse("""{ "user": {"email":"john-doe@gmail.com", "password":"pass"} }"""))
      val create = route(app, request).get

      status(create) mustBe CREATED
      contentType(create) mustBe Some("application/json")
      contentAsString(create) must include("user")
      contentAsString(create) must include("authToken")
    }
  }
}
