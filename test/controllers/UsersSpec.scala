import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

class UsersSpec extends PlaySpec with BeforeAndAfterAll {
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

  "Users" should {
    "renders the index response" in {
      DatabaseInserter.insert("Users", 12, List("john-doe_index@gmail.com", "John", "Doe", "password", "token", "2016-01-01", "User", "1", "999999", "2016-01-01"))
      val index = route(app, FakeRequest(GET, "/users")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("application/json")
      contentAsString(index) must include("users")
      contentAsString(index) mustNot include("authToken")
    }

    "show reponse" should {
      "responses 404 if there is no such user" in {
        val show = route(app, FakeRequest(GET, "/users/1")).get

        status(show) mustBe NOT_FOUND
        contentType(show) mustBe Some("application/json")
        contentAsString(show) mustNot include("user")
      }

      "responses 200 if record exists" in {
        val id = 10
        DatabaseInserter.insert("Users", id, List("john-doe@gmail.com", "John", "Doe", "password", "token", "2016-01-01", "User", "1", "999999", "2016-01-01"))

        val show = route(app, FakeRequest(GET, s"/users/$id")).get

        status(show) mustBe OK
        contentAsString(show) must include("user")
        contentAsString(show) mustNot include("authToken")
      }
    }

    "creating" should {
      "returns 400 if bad request" in {
        val request = FakeRequest(POST, "/users").withJsonBody(Json.parse("""{ "user": {"password":"pass"} }"""))
        val create = route(app, request).get

        status(create) mustBe BAD_REQUEST
        contentType(create) mustBe Some("application/json")
        contentAsString(create) must include("errors")
        contentAsString(create) must include("\"email\":[\"can't be blank\"]")
      }

      "returns 201 if request is acceptable" in {
        DatabaseCleaner.clean(List("Users"))

        val request = FakeRequest(POST, "/users").withJsonBody(Json.parse("""{ "user": {"email":"john-doe@gmail.com", "password":"pass"} }"""))
        val create = route(app, request).get

        status(create) mustBe CREATED
        contentType(create) mustBe Some("application/json")
        contentAsString(create) must include("user")
        contentAsString(create) must include("authToken")
      }
    }
  }
}
