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
      val index = route(app, FakeRequest(GET, "/users")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("application/json")
      contentAsString(index) must include("users")
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
        DatabaseInserter.insert("Users", id, List("john-doe@gmail.com", "John", "Doe", "password", "User", "1", "999999", "2016-01-01"))

        import models._
        println(Users.all)
        val show = route(app, FakeRequest(GET, s"/users/$id")).get

        status(show) mustBe OK
        contentAsString(show) must include("user")
      }
    }
  }
}
