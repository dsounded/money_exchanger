package validators.user

import com.wix.accord.dsl._
import com.wix.accord.{validate => validateUser}

import models.{User, Users}

import validators.BaseValidator
import validators.BaseValidator.validateWithErrors

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Validator {
  implicit val userValidator = validator[User] { user =>
    user.email is notEmpty
    user.password is notEmpty
  }
  def validate(record: User): Future[Boolean] = {
    for {
      accordValidation <- validateWithErrors(validateUser(record), record)
      customValidation <- customValidation(record)
    } yield accordValidation && customValidation
  }

  private def customValidation(record: User): Future[Boolean] = {
    for {
      emailValidation <- BaseValidator.unique(Users, record, "email", record.email)
      phoneValidation <- BaseValidator.unique(Users, record, "phone", record.phone)
    } yield emailValidation && phoneValidation
  }
}
