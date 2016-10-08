package users

object Role extends Enumeration {
  val User =      Value("User")
  val Moderator = Value("Moderator")
  val Admin =     Value("Admin")
}
