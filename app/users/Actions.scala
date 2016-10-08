package users

import play.api.libs.json.JsValue
import play.api.mvc.Request

import users.{Validator => UserValidator}
import users.RequestSerializer.deserialize

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object Actions {
  def create(request: Request[JsValue])(implicit ec: ExecutionContext) = {
    val record = request.body.as[User](deserialize)

    UserValidator.validate(record) map { validator =>
      if (validator) {
        Users.create(record)
        val userFromDB = Users.findByEmail(record.email).map(_.get)

        (userFromDB, true)
      } else (Future.successful(record), false)
    }
  }
}
