package models

import java.sql.Timestamp

import slick.driver.MySQLDriver.api._

import utils.TimeUtil

import serializers.JsonFormatters

case class Country(id: Long = 0, title: String, abbreviation: String, isActive: Boolean = true, createdAt: Timestamp = TimeUtil.now) extends Errorable with Defaultable

case object Country extends JsonFormatters {
  def default = {
    val country = new Country(title = "", abbreviation = "")
    country.setDefault
    country
  }
}

class CountriesTable(tag: Tag) extends RichTable[Country](tag, "Countries") {
  def title =        column[String]("title")
  def abbreviation = column[String]("abbreviation")
  def isActive =     column[Boolean]("isActive")
  def createdAt =    column[Timestamp]("createdAt")

  def * =
    (id, title, abbreviation, isActive, createdAt) <> ((Country.apply _).tupled, Country.unapply)
}

object Countries extends QueryCommands[CountriesTable, Country] {
  override val tableName = "Countries"

  override val records = TableQuery[CountriesTable]

  def findByTitle(title: String) = dbConfig.db.run(records.filter(_.title === title).result.head)
}