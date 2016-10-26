package countries

import com.wix.accord.dsl._
import com.wix.accord.{validate => validateCountry}

import validators.BaseValidator
import validators.BaseValidator.validateWithErrors

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Validator {
  implicit val countryValidator = validator[Country] { country =>
    country.title is notEmpty
    country.abbreviation is notEmpty
  }

  def validate(record: Country): Future[Boolean] = {
    for {
      accordValidation <- validateWithErrors(validateCountry(record), record)
      customValidation <- customValidation(record)
    } yield accordValidation && customValidation
  }

  private def customValidation(record: Country): Future[Boolean] = {
    for {
      titleValidation <- BaseValidator.unique(Countries, record, "title", record.title)
      abbreviationValidation <- BaseValidator.unique(Countries, record, "abbreviation", record.abbreviation)
    } yield titleValidation && abbreviationValidation
  }

  def validate(record: Future[Option[Country]]): Future[Option[(Future[(Boolean, Country)])]] = {
    record.map { theRecord =>
      theRecord.map { existingRecord =>
        validate(existingRecord) map { isValid =>
          (isValid, existingRecord)
        }
      }
    }
  }
}
