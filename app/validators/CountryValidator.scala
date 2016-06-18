package validators

import models.{Country, CountriesTable, Countries}
import validators.PersistenceValidator

object CountryValidator {
  def validate(record: Country): Boolean = {
    PersistenceValidator.validate[Country](record, "title", record.title) &&
      PersistenceValidator.validate[Country](record, "abbreviation", record.abbreviation) &&
      UniquenessValidator.validate[Country](Countries, record, "title", record.title) &&
      UniquenessValidator.validate[Country](Countries, record, "abbreviation", record.abbreviation)
  }
}