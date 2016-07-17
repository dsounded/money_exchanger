package serializers.user

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.libs.functional.syntax._

import enumerators.user.Role

import models.User

import services.user.Tokenizer

import utils.TimeUtil

object RequestSerializer {
  val DefaultValue = ""
  val DefaultIntValue = 0

  def toModel(request: JsValue): User = {
    val email = Try((request \ "email").as[String]).getOrElse(DefaultValue)
    val firstName = Try((request \ "firstName").as[String]).getOrElse(DefaultValue)
    val lastName = Try((request \ "lastName").as[String]).getOrElse(DefaultValue)
    val password = Try((request \ "password").as[String]).getOrElse(DefaultValue)
    val cityId = Try((request \ "cityId").as[Int]).getOrElse(DefaultIntValue)
    val phone = Try((request \ "phone").as[String]).getOrElse(DefaultValue)

    new User(0, email, firstName, lastName, Role.User, password,
             authToken = Tokenizer.make(List(email, firstName, lastName, password)),
             authTokenCreatedAt = TimeUtil.now, cityId, phone)
  }
}