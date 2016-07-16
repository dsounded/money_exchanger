package enumerators.user

import slick.driver.MySQLDriver.api.{MappedColumnType, stringColumnType}

object Role extends Enumeration {
  val User =      Value("User")
  val Moderator = Value("Moderator")
  val Admin =     Value("Admin")
}
