package responders

import javax.inject.Inject

import countries.Country
import users.User

import models._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import collection.mutable.{Map => MutableMap}

class Responder[A <: Errorable] @Inject() (implicit val ex: ExecutionContext){
  val Success = 200
  val Created = 201
  val BadRequest = 400
  val NoContent = 204
  val NotFound = 404
  val Forbidden = 403
  val NotFoundRecord = Set("not found")
  val ErrorRootName = "errors"
  val ExpiredTokenError = Set("token has expired")
  val UserNotFound = Set("token is not valid")
  val EmailNotFound = Set("email is invalid")
  val WrongPassword = Set("wrong password")
  val UserRootName = "user"
  val NotAllowed = Set("action is not allowed")

  type CreateTypedRespond = Future[(Either[Future[A], Future[MutableMap[String,Set[String]]]], String, Int)]
  def create(data: Future[(Future[A], Boolean)], rootName: String): CreateTypedRespond = {
    data map { theData =>
      val (record, isSuccess) = theData

      if (isSuccess) (Left(record), rootName, Created) else (Right(record.map(r => r.errors)), ErrorRootName, BadRequest)
    }
  }

  def destroy(data: Future[Boolean]): Future[Either[Int, ((String, Set[String]), Int)]] = {
    data.map(theData => if (theData) Left(NoContent) else Right((ErrorRootName -> NotFoundRecord) -> NotFound))
  }

  type UpdateTypedRespond = Future[Future[Either[Int, (MutableMap[String,Set[String]], String, Int)]]]
  def update(record: Future[Future[(Boolean, Country)]]): UpdateTypedRespond = {
    record map { theRecord =>
      theRecord map { theValidationWithObject =>
        val (isValid, theExistingRecord) = theValidationWithObject
        if (isValid) {
          Left(NoContent)
        } else if (theExistingRecord.isDefault) Left(NotFound) else Right(theExistingRecord.errors, ErrorRootName, BadRequest)
      }
    }
  }

  type ShowTypedRespond = Future[(Either[A, Set[String]], String, Int)]
  def show(record: Future[Option[A]], rootName: String): ShowTypedRespond = {
    record map {
      case Some(existingRecord) => (Left(existingRecord), rootName, Success)
      case _                    => (Right(NotFoundRecord), ErrorRootName, NotFound)
    }
  }

  type AuthRespond = Future[(Either[Object, Set[String]], String, Int)]
  def authorize(authInfo: Future[(Object, Set[String])]): AuthRespond = {
    authInfo map {
      case (user, errors) if errors isEmpty => (Left(user), UserRootName, Created)
      case (_, errors) => (Right(errors), ErrorRootName, BadRequest)
    }
  }
}
