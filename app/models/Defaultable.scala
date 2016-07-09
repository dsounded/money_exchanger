package models

trait Defaultable {
  var isDefault = false

  def setDefault: Unit = isDefault = true
}