package models

import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.Play
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class RichTable[T](tag: Tag, val name: String) extends Table[T](tag, name) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}

trait QueryCommands[T <: RichTable[A], A] {
  val records: TableQuery[T]
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  def tableName: String = ???

  def find(id: Long) = {
    dbConfig.db.run(findQuery(id).result.headOption)
  }

  def all = {
    dbConfig.db.run(records.result)
  }

  def destroy(id: Long) = {
    dbConfig.db.run(findQuery(id).delete)
  }

  def create(record: T#TableElementType) = {
    dbConfig.db.run(records += record)
  }

  def update(id: Long, row: T#TableElementType) {
    var record = findQuery(id)

    dbConfig.db.run(record.update(row))
  }

  def none = dbConfig.db.run(records.filter(record => record.id.asColumnOf[Int] === -1).result.head)

  def exists(tableName: String, column: String, value: Any): Future[Boolean] = {
    val query = dbConfig.db.run(sql"SELECT COUNT(1) from #$tableName WHERE #$column = ${value.toString};".as[Int])

    query.map(_.sum > 0)
  }

  protected def findQuery(id: Long) = {
    records.filter(_.id === id)
  }
}
