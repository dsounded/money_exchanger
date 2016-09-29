package serializers.country

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.{Reads, __, JsValue}
import play.api.libs.functional.syntax._

import models.Country

object RequestSerializer {
  val DefaultValue = ""

  def deserialize(id: Long = 0, defaultTitle: String = DefaultValue, defaultAbbr: String = DefaultValue): Reads[Country] = (
    (__ \ 'country \ 'title).readNullable[String] and
    (__ \ 'country \ 'abbreviation).readNullable[String]
  )((titleOpt: Option[String], abbreviationOpt: Option[String]) => {
     val title = titleOpt.getOrElse(defaultTitle)
     val abbreviation = abbreviationOpt.getOrElse(defaultAbbr)

    Country(id, title, abbreviation, true)
  })

  def deserialize(request: JsValue, record: Future[Option[Country]]): Future[Option[Country]] = {
    record map { theRecord =>
      theRecord.map(exitingRecord => request.as[Country](deserialize(exitingRecord.id, exitingRecord.title, exitingRecord.abbreviation)))
    }
  }
}
