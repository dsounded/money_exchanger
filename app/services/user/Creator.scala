package services.user

import javax.inject.Inject

import play.api.libs.json.JsValue
import play.api.mvc.Request

import validators.user.{Validator => UserValidator}
import serializers.user.RequestSerializer.deserialize
import models.{User, Users}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class Creator @Inject() (request: Request[JsValue])(implicit ec: ExecutionContext) {
  def create: Future[(Future[User], Boolean)] = {
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

object Creator {
  def create(request: Request[JsValue])(implicit ec: ExecutionContext) = {
    val creator = new Creator(request)

    creator.create
  }
}
