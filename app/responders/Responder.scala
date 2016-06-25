package responders

import models._
import scala.concurrent._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

class Responder[A <: Errorable] {
  val Created = 201
  val BadRequest = 400

  type TypedRespond = (Either[Future[A], Future[Map[String,String]]], String, Int)
  def create(data: (Future[A], Boolean), rootName: String): TypedRespond = {
    val (record, isSuccess) = data

    if (isSuccess) (Left(record), rootName, Created) else (Right(record.map(r => r.errors)), "errors", BadRequest)
  }
}