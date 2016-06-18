package services

import play.api.libs.json._
import play.api.mvc._
import validators.CountryValidator
import serializers.CountryRequestSerializer
import models.{Country, Countries}
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class CountryCreator(request: Request[JsValue]) {
  def create: (Country,Boolean) = {

    val parsedRequest = request.body.as[Map[String, JsValue]]
    val record = CountryRequestSerializer.toModel(parsedRequest("country"))

    if (CountryValidator.validate(record)) {
      Countries.create(record)
      val countryFromDB = Await.result(Countries.findByTitle(record.title), 10 seconds)

      (countryFromDB, true)
    } else (record, false)
  }
}

object CountryCreator {
  def create(request: Request[JsValue]) = {
    val creator = new CountryCreator(request)

    creator.create
  }
}