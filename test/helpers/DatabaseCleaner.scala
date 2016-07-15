package test.helpers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import play.api.Play

object DatabaseCleaner {
  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def clean(tables: List[String]) = tables.foreach(tableName => dbConfig.db.run(sqlu"TRUNCATE #$tableName;"))
}