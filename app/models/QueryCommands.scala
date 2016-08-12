package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import play.api.Play

abstract class RichTable[T](tag: Tag, val name: String) extends Table[T](tag, name) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}

trait QueryCommands[T <: RichTable[A], A] {
  val records: TableQuery[T]
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  def tableName: String = ???

  def all                                       = dbConfig.db.run(records.result)
  def find(id: Long)                            = dbConfig.db.run(findQuery(id).result.headOption)
  def create(record: T#TableElementType)        = dbConfig.db.run(records += record)
  def update(id: Long, row: T#TableElementType) = dbConfig.db.run(findQuery(id).update(row))
  def destroy(id: Long)                         = dbConfig.db.run(findQuery(id).delete)
  def none                                      = dbConfig.db.run(records.filter(record => record.id.asColumnOf[Int] === -1).result.head)

  def exists(id: Long = 0, tableName: String, column: String, value: Any): Future[Boolean] =
    dbConfig.db.run(sql"SELECT id from #$tableName WHERE #$column = ${value.toString};".as[Int]) map {
      theQuery => theQuery.nonEmpty && !theQuery.contains(id)
    }

  protected def findQuery(id: Long) = {
    records.filter(_.id === id)
  }
}
