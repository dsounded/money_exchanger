package services

import play.api.libs.json.JsValue
import play.api.mvc.Request

import validators.CountryValidator
import serializers.CountryRequestSerializer
import models.{Country, Countries}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CountryCreator(request: Request[JsValue]) {
  def create: Future[(Future[Country], Boolean)] = {

    val parsedRequest = request.body.as[Map[String, JsValue]]
    val record = CountryRequestSerializer.toModel(parsedRequest("country"))

    CountryValidator.validate(record).map { validator =>
      if (validator) {
        Countries.create(record)
        val countryFromDB = Countries.findByTitle(record.title)

        (countryFromDB, true)
      } else (Future.successful { record }, false)
    }
  }
}

object CountryCreator {
  def create(request: Request[JsValue]) = {
    val creator = new CountryCreator(request)

    creator.create
  }
}