package models

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.MySQLDriver.api._

import utils.TimeUtil

import java.sql.Timestamp

import enumerators.user.{Role => UserRole}
import serializers.JsonFormatters

case class User(id: Long = 0, email: String, firstName: String, lastName: String,
                role: UserRole.Value, password: String, authToken: String,
                authTokenCreatedAt: Timestamp, cityId: Long, phone: String,
                createdAt: Timestamp = TimeUtil.now) extends Errorable with Defaultable

object User extends JsonFormatters

class UsersTable(tag: Tag) extends RichTable[User](tag, "Users") {
  def email =              column[String]("email")
  def firstName =          column[String]("firstName")
  def lastName =           column[String]("lastName")
  def role =               column[UserRole.Value]("role")
  def password =           column[String]("password")
  def authToken =          column[String]("authToken")
  def authTokenCreatedAt = column[Timestamp]("authTokenCreatedAt")
  def cityId =             column[Long]("cityId")
  def phone =              column[String]("phone")
  def createdAt =          column[Timestamp]("createdAt")

  def * =
    (id, email, firstName, lastName, role, password, authToken, authTokenCreatedAt, cityId, phone, createdAt) <> ((User.apply _).tupled, User.unapply)

  implicit val userEnumMapper = MappedColumnType.base[UserRole.Value, String](
    enum => enum.toString,
    string => UserRole.withName(string)
  )
}

object Users extends QueryCommands[UsersTable, User] {
  override val tableName = "Users"

  override val records = TableQuery[UsersTable]

  override def create(record: User) = {
    import com.github.t3hnar.bcrypt._

    super.create(record.copy(password = record.password.bcrypt))
  }

  def findByEmail(email: String) = dbConfig.db.run(records.filter(_.email === email).result.head)

  def findByAuthToken(token: String) = dbConfig.db.run(records.filter(_.authToken === token).result.headOption)
}
