package models

import collection.mutable.{ Map => MutableMap }

trait Errorable {
  def id: Long

  private var errorsList = MutableMap.empty[String,Set[String]]

  def errors = errorsList

  def addError(error: Error): MutableMap[String, Set[String]] = {
    errorsList += error.key -> (errorsList.get(error.key).getOrElse(Set[String]()) + error.message)
  }
}

case class Error(val key: String, val message: String)
