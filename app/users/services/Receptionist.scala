package users

import utils.TimeUtil

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Try
import users.RequestSerializer.DefaultValue

import responders.Responder

import play.api.libs.json.JsValue
import play.api.mvc.Request

object Receptionist {
  val DaysActive = 12

  def canEnter(user: User): Boolean = TimeUtil.addDays(user.authTokenCreatedAt, DaysActive).after(TimeUtil.now)

  def checkList(params: Request[JsValue]): Future[(Object, Set[String])] = {
    val (email, password) = (Try((params.body \ "email").as[String]).getOrElse(DefaultValue), Try((params.body \ "password").as[String]).getOrElse(DefaultValue))

    getUser(email, password)
  }

  private def getUser(email: String, password: String) = {
    import com.github.t3hnar.bcrypt._

    Users.findByEmail(email) map {
      case Some(user) if password.isBcrypted(user.password) => (user, Set.empty[String])
      case Some(user) => (user, responder.WrongPassword)
      case None => (Users.none, responder.EmailNotFound)
    }
  }

  private lazy val responder = new Responder
}
