package services.user

import play.api.libs.json.JsValue
import play.api.mvc.Request

import validators.user.{Validator => UserValidator}
import serializers.user.{RequestSerializer => UserRequestSerializer}
import models.{User, Users}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Creator(request: Request[JsValue]) {
  def create: Future[(Future[User], Boolean)] = {

    val parsedRequest = request.body.as[Map[String, JsValue]]
    val record = UserRequestSerializer.toModel(parsedRequest("user"))

    UserValidator.validate(record) map { validator =>
      if (validator) {
        Users.create(record)
        val userFromDB = Users.findByEmail(record.email)

        (userFromDB, true)
      } else (Future.successful(record), false)
    }
  }
}

object Creator {
  def create(request: Request[JsValue]) = {
    val creator = new Creator(request)

    creator.create
  }
}
