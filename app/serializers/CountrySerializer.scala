package serializers

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._

object CountryRequestSerializer {
  val DefaultValue = ""

  def toModel(request: JsValue): Country = {
    val title = Try((request \ "title").as[String]).getOrElse(DefaultValue)
    val abbreviation = Try((request \ "abbreviation").as[String]).getOrElse(DefaultValue)

    new Country(0, title, abbreviation, true)
  }

  def toModel(request: JsValue, record: Future[Option[Country]]): Future[Option[Country]] = {
    record.map { theRecord =>
      theRecord.map(exitingRecord => exitingRecord.copy(title = Try((request \ "title").as[String]).getOrElse(exitingRecord.title),
                                                        abbreviation = Try((request \ "abbreviation").as[String]).getOrElse(exitingRecord.abbreviation)))
    }
  }
}