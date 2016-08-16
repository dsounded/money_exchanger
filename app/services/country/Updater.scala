package services.country

import javax.inject.Inject

import play.api.libs.json.JsValue
import play.api.mvc.Request

import models.{Countries, Country}
import serializers.country.{RequestSerializer => CountryRequestSerializer}
import validators.country.{Validator => CountryValidator}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class Updater @Inject() (id: Long, request: Request[JsValue])(implicit ec: ExecutionContext) {
  def update: Future[Future[(Boolean, Country)]] = {
    val parsedRequest = request.body.as[Map[String, JsValue]]

    val country = Countries.find(id)
    val updatedCountry = CountryRequestSerializer.toModel(parsedRequest("country"), country)

    CountryValidator.validate(updatedCountry) map { validator =>
      validator.getOrElse(Future.successful((false, Country.default))) map { existingValidator =>
        val (isValid, updatedRecord) = existingValidator

        if (isValid) {
          Countries.update(updatedRecord.id, updatedRecord)
          (true, updatedRecord)
        } else (false, updatedRecord)
      }
    }
  }
}

object Updater {
  def update(id: Long, request: Request[JsValue])(implicit ec: ExecutionContext) = {
    val updater = new Updater(id, request)
    updater.update
  }
}