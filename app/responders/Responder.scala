package responders

import models._
import scala.concurrent._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

class Responder[A] {
  type TypedRespond = (A, String, Int)
  def create(data: (A,Boolean), rootName: String): TypedRespond = {
    val (record, isSuccess) = data

    if (isSuccess) (record, rootName, 201) else (record, "errors", 400)
  }
}