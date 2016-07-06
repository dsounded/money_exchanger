package models

import collection.mutable.{ Map => MutableMap }

trait Errorable {
  private var errorsList = MutableMap.empty[String,Set[String]]

  def errors = errorsList

  def addError(error: (String,String)) = errorsList(error._1) = if (errorsList isDefinedAt error._1) errorsList(error._1) + error._2 else Set(error._2)
}