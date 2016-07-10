package responders

import models._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import collection.mutable.{ Map => MutableMap }

class Responder[A <: Errorable] {
  val Success = 200
  val Created = 201
  val BadRequest = 400
  val NoContent = 204
  val NotFound = 404
  val NotFoundRecord = Set("not found")
  val ErrorRootName = "errors"

  type CreateTypedRespond = Future[(Either[Future[A], Future[MutableMap[String,Set[String]]]], String, Int)]
  def create(data: Future[(Future[A], Boolean)], rootName: String): CreateTypedRespond = {
    data map { theData =>
      val (record, isSuccess) = theData

      if (isSuccess) (Left(record), rootName, Created) else (Right(record.map(r => r.errors)), ErrorRootName, BadRequest)
    }
  }

  def destroy(data: Future[Boolean]): Future[Either[Int, ((String, Set[String]), Int)]] = {
    data.map(theData => if (theData) Left(NoContent) else Right(((ErrorRootName -> NotFoundRecord) -> NotFound)))
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
  def show(record: Future[Option[A]], rooName: String): ShowTypedRespond = {
    record map { theRecord =>
      theRecord match {
        case Some(existingRecord) => (Left(existingRecord), rooName, Success)
        case _                    => (Right(NotFoundRecord), ErrorRootName, NotFound)
      }
    }
  }
}
