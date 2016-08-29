package services.country

import javax.inject.Inject

import play.api.libs.json.JsValue
import play.api.mvc.Request

import validators.country.{Validator => CountryValidator}
import serializers.country.RequestSerializer.deserialize
import models.{Country, Countries}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class Creator @Inject() (request: Request[JsValue])(implicit val ec: ExecutionContext) {
  def create: Future[(Future[Country], Boolean)] = {
    val record = request.body.as[Country](deserialize())

    CountryValidator.validate(record) map { validator =>
      if (validator) {
        Countries.create(record)
        val countryFromDB = Countries.findByTitle(record.title)

        (countryFromDB, true)
      } else (Future.successful { record }, false)
    }
  }
}

object Creator {
  def create(request: Request[JsValue])(implicit ec: ExecutionContext) = {
    val creator = new Creator(request)

    creator.create
  }
}
