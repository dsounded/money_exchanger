package test.helpers

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import play.api.Play

object DatabaseInserter {
  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def insert(table: String, id: Long = 0, values: List[String]) = {
    val valuesWithId = id.toString :: values
    val valuesWithQuotes = valuesWithId.map(value => s""""$value"""")

    dbConfig.db.run(sqlu"INSERT into #$table VALUES(#${valuesWithQuotes.mkString(",")});")
  }
}
