package validators

import java.util.concurrent.TimeUnit

import com.wix.accord.{NullSafeValidator, Result, Success, Failure, RuleViolation, GroupViolation}
import com.wix.accord.ViolationBuilder._

import models.{QueryCommands, Errorable}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object BaseValidator {
  val UniqueError = "value has already been taken"

  def unique[T <: Errorable](searcher: QueryCommands[_, _], record: T, column: String, value: String): Future[Boolean] = {
    if (value isEmpty) return Future.successful(true)

    searcher.exists(record.id, searcher.tableName, column, value).map { result =>
      if (result) {
        record.addError(column -> UniqueError)

        false
      } else true
    }
  }

  def validateWithErrors[T <: Errorable](validationResult: Result, record: T) = {
    validationResult match {
      case Success => Future.successful(true)
      case Failure(violations) => violations.foreach {
        case RuleViolation(value, message, Some(field)) => record.addError(field -> message)
        case GroupViolation(value, message, Some(field), children) => record.addError(field -> message)
        case _ => ???
      }
    }
    Future.successful(validationResult isSuccess)
  }
}
