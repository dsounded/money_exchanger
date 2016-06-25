package serializers

import play.api.libs.json.Json._
import play.api.libs.json._
import org.joda.time.DateTime
import java.sql.Timestamp
import models.Country

trait Timestamps {
  def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t.getTime)

  def dateTimeToTimestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)

  implicit val timestampFormat = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = toJson(timestampToDateTime(t))

    def reads(json: JsValue): JsResult[Timestamp] = fromJson[DateTime](json).map(dateTimeToTimestamp)
  }
  implicit val jsonFormat = Json.format[Country]
}