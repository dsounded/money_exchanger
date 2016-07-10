package services.country

import play.api.libs.json.JsValue
import play.api.mvc.Request

import validators.country.{Validator => CountryValidator}
import serializers.country.{RequestSerializer => CountryRequestSerializer}
import models.{Country, Countries}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Creator(request: Request[JsValue]) {
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

object Creator {
  def create(request: Request[JsValue]) = {
    val creator = new Creator(request)

    creator.create
  }
}