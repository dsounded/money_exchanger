package models

trait Errorable {
  private var errorsList: Map[String,String] = Map()

  def errors = errorsList

  def addError(error: (String,String)) = errorsList = errorsList + error
}