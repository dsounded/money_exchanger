package services.country

import play.api.libs.json.JsValue
import play.api.mvc.Request

import models.{Countries, Country}
import serializers.country.{RequestSerializer => CountryRequestSerializer}
import validators.country.{Validator => CountryValidator}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Updater(id: Long, request: Request[JsValue]) {
  def update: Future[Future[(Boolean, Country)]] = {
    val parsedRequest = request.body.as[Map[String, JsValue]]

    val country = Countries.find(id)
    val updatedCountry = CountryRequestSerializer.toModel(parsedRequest("country"), country)

    val validator = CountryValidator.validate(updatedCountry)
    validator map { theValidator =>
      val safeValidator = theValidator.getOrElse(Future.successful((false, Country.default)))
      safeValidator map { existingValidator =>
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
  def update(id: Long, request: Request[JsValue]) = {
    val updater = new Updater(id, request)
    updater.update
  }
}