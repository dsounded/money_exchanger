package validators.user

import models.{User, Users}
import validators.{PersistenceValidator, UniquenessValidator}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Validator {
  def validate(record: User): Future[Boolean] = {
    for { isEmailPersist    <- Future.successful { PersistenceValidator.validate[User](record, "email", record.email) }
          isPasswordPersist <- Future.successful { PersistenceValidator.validate[User](record, "password", record.password) }
          isEmailUnique     <- UniquenessValidator.validate[User](Users, record, "email", record.email)
          isPhoneUnique     <- UniquenessValidator.validate[User](Users, record, "phone", record.phone)
    } yield isEmailPersist && isPasswordPersist && isEmailUnique && isPhoneUnique
  }
}
