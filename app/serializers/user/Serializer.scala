package serializers.user

import play.api.libs.json.{__, Reads}
import play.api.libs.functional.syntax._

import enumerators.user.Role

import models.User

import services.user.Tokenizer

import utils.TimeUtil

object RequestSerializer {
  val DefaultValue = ""
  val DefaultIntValue = 0

  def deserialize: Reads[User] = (
    (__ \ 'user \ 'email).readNullable[String] and
    (__ \ 'user \ 'firstName).readNullable[String] and
    (__ \ 'user \ 'lastName).readNullable[String] and
    (__ \ 'user \ 'password).readNullable[String] and
    (__ \ 'user \ 'cityId).readNullable[Long] and
    (__ \ 'user \ 'phone).readNullable[String]
  )((emailOpt: Option[String], firstNameOpt: Option[String], lastNameOpt: Option[String],
     passwordOpt: Option[String], cityIdOpt: Option[Long], phoneOpt: Option[String]) => {
      val email = emailOpt.getOrElse(DefaultValue)
      val firstName = firstNameOpt.getOrElse(DefaultValue)
      val lastName = lastNameOpt.getOrElse(DefaultValue)
      val password = passwordOpt.getOrElse(DefaultValue)

      new User(0, email, firstName, lastName, Role.User, password,
        authToken = Tokenizer.make(List(email, firstName, lastName, password)),
        authTokenCreatedAt = TimeUtil.now, cityIdOpt.getOrElse(DefaultIntValue), phoneOpt.getOrElse(DefaultValue))
    }
  )
}