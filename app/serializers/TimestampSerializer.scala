package serializers

import play.api.libs.json.Json._
import play.api.libs.json._

import org.joda.time.DateTime
import java.sql.Timestamp

import models.{Country, User}

import enumerators.user.{Role => UserRole}

trait JsonFormatters {
  def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t.getTime)

  def dateTimeToTimestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)

  implicit val timestampFormat = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = toJson(timestampToDateTime(t))

    def reads(json: JsValue): JsResult[Timestamp] = fromJson[DateTime](json).map(dateTimeToTimestamp)
  }

  implicit val enumFormat = new Format[UserRole.Value] {
    def writes(enum: UserRole.Value): JsValue = toJson(enum.toString)

    def reads(json: JsValue): JsResult[UserRole.Value] = fromJson[String](json).map(json => UserRole.withName(json.toString))
  }

  implicit val jsonCountryFormat = Json.format[Country]
  implicit val jsonUserWriter = new Writes[User] {
    def writes(user: User) = {
      defaultObjectCreator(user)
    }

    def writesForCreate(user: User) = {
      defaultObjectCreator(user) + ("authToken" -> Json.toJson(user.authToken))
    }

    def defaultObjectCreator(user: User) = {
      Json.obj("id" -> user.id,
               "email" -> user.email,
               "firstName" -> user.firstName,
               "lastName" -> user.lastName,
               "role" -> user.role,
               "cityId" -> user.cityId,
               "phone" -> user.phone,
               "createdAt" -> user.createdAt)
    }
  }
}