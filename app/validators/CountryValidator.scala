package validators

import models.{Country, CountriesTable, Countries}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CountryValidator {
  def validate(record: Country): Future[Boolean] = {
    PersistenceValidator.validate[Country](record, "title", record.title)
    PersistenceValidator.validate[Country](record, "abbreviation", record.abbreviation)
    UniquenessValidator.validate[Country](Countries, record, "title", record.title).map(validationResult => validationResult)
    UniquenessValidator.validate[Country](Countries, record, "abbreviation", record.abbreviation).map(validationResult => validationResult)

    Future { record.errors.isEmpty }
  }
}
