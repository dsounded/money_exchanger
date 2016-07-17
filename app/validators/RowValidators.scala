package validators

import models.{Errorable, Countries, CountriesTable, Country, QueryCommands}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait BaseValidator {
  implicit def anyToString(a: Any): String = a.toString
}

object PersistenceValidator extends BaseValidator{
  val errorText = "can't be blank"

  def validate[A <: Errorable](record: A, field: String, row: String): Boolean = {
    if (row isEmpty) {
      record.addError(field -> errorText)

      false
    } else true
  }
}

object UniquenessValidator extends BaseValidator{
  val errorText = "value has already been taken"

  def validate[A <: Errorable](searcher: QueryCommands[_,_], record: A, field: String, row: String): Future[Boolean] = {
    if (row isEmpty) return Future.successful(true)

    val tableName = searcher.tableName

    searcher.exists(record.id, tableName, field, row).map { result =>
      if (result) {
        record.addError(field -> errorText)

        false
      } else true
    }
  }
}
