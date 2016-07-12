package validators.country

import models.{Country, CountriesTable, Countries}
import validators.{PersistenceValidator, UniquenessValidator}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Validator {
  def validate(record: Country): Future[Boolean] = {
    for { isTitlePersist <- Future.successful { PersistenceValidator.validate[Country](record, "title", record.title) }
          isAbbrPersist  <- Future.successful { PersistenceValidator.validate[Country](record, "abbreviation", record.abbreviation) }
          isTitleUnique  <- UniquenessValidator.validate[Country](Countries, record, "title", record.title)
          isAbbrUnique   <- UniquenessValidator.validate[Country](Countries, record, "abbreviation", record.abbreviation)
    } yield isTitlePersist && isAbbrPersist && isTitleUnique && isAbbrUnique
  }

  def validate(record: Future[Option[Country]]): Future[Option[(Future[(Boolean, Country)])]] = {
    record.map { theRecord =>
      theRecord.map { existingRecord =>
        for { isTitlePersist <- Future.successful { PersistenceValidator.validate[Country](existingRecord, "title", existingRecord.title) }
              isAbbrPersist  <- Future.successful { PersistenceValidator.validate[Country](existingRecord, "abbreviation", existingRecord.abbreviation) }
              isTitleUnique  <- UniquenessValidator.validate[Country](Countries, existingRecord, "title", existingRecord.title)
              isAbbrUnique   <- UniquenessValidator.validate[Country](Countries, existingRecord, "abbreviation", existingRecord.abbreviation)
        } yield (isTitlePersist && isAbbrPersist && isTitleUnique && isAbbrUnique, existingRecord)
      }
    }
  }
}
