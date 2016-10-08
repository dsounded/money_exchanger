package countries

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.JsValue
import play.api.mvc.Request

import countries.RequestSerializer.deserialize
import countries.{Validator => CountryValidator}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object Actions {
  def update(id: Long, request: Request[JsValue])(implicit ec: ExecutionContext) = {
    val country = Countries.find(id)
    val updatedCountry = deserialize(request.body, country)

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

  def create(request: Request[JsValue])(implicit ec: ExecutionContext): Future[(Future[Country], Boolean)] = {
    val record = request.body.as[Country](deserialize())

    CountryValidator.validate(record) map { validator =>
      if (validator) {
        Countries.create(record)
        val countryFromDB = Countries.findByTitle(record.title)

        (countryFromDB, true)
      } else (Future.successful { record }, false)
    }
  }

  def destroy(id: Long)(implicit ec: ExecutionContext): Future[Boolean] = Countries.destroy(id)

  private implicit def intToBoolean(i: Future[Int]): Future[Boolean] = i.map(_ != 0)
}
