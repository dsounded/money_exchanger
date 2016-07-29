import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import play.api.libs.json._

import test.helpers.{DatabaseCleaner, DatabaseInserter}

import play.api.Play

import com.github.t3hnar.bcrypt._

class SessionCreateSpec extends PlaySpec with BeforeAndAfterAll {
  val app = new GuiceApplicationBuilder().build

  override def beforeAll() {
    Play.maybeApplication match {
      case Some(app) => ()
      case _ => Play.start(app)
    }

    DatabaseCleaner.clean(List("Users"))

    val password = "password".bcrypt

    DatabaseInserter.insert("Users", 12, List("john-doe_session@gmail.com", "John", "Doe", password, "token", "2016-01-01", "User", "1", "999999", "2016-01-01"))
  }

  override def afterAll() {
    DatabaseCleaner.clean(List("Users"))
  }

  "creates session if data is ok" in {
    val request = FakeRequest(POST, "/session").withJsonBody(Json.parse("""{ "email":"john-doe_session@gmail.com", "password":"password" }"""))
    val create = route(app, request).get

    status(create) mustBe CREATED
    contentType(create) mustBe Some("application/json")
    contentAsString(create) must include("user")
    contentAsString(create) must include("authToken")
  }

  "responses with error if data is not ok" in {
    val request = FakeRequest(POST, "/session").withJsonBody(Json.parse("""{ "email":"john-doe_session@gmail.com", "password":"bad_password" }"""))
    val create = route(app, request).get

    status(create) mustBe BAD_REQUEST
    contentType(create) mustBe Some("application/json")
    contentAsString(create) must include("error")
  }
}
