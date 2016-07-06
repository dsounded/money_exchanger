package validators

import models.{Country, CountriesTable, Countries}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CountryValidator {
  def validate(record: Country): Future[Boolean] = {
    for { isTitlePersist <- Future.successful { PersistenceValidator.validate[Country](record, "title", record.title) }
          isAbbrPersist  <- Future.successful { PersistenceValidator.validate[Country](record, "abbreviation", record.abbreviation) }
          isTitleUnique  <- UniquenessValidator.validate[Country](Countries, record, "title", record.title)
          isAbbrUnique   <- UniquenessValidator.validate[Country](Countries, record, "abbreviation", record.abbreviation)
    } yield isTitlePersist && isAbbrPersist && isTitleUnique && isAbbrUnique
  }
}
