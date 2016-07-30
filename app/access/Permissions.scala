package access

import enumerators.user.Role.{User => UserRole, Moderator, Admin}

import models.User

object Permissions {
  val Permissions = Map(
    UserRole -> Set("Countries.show", "Countries.index", "Users.index", "Users.show"),
    Moderator -> Set("Countries.show", "Countries.index", "Countries.create", "Countries.update",
                     "Users.index", "Users.show"),
    Admin -> Set("Countries.*", "Users.*")
  )

  def can(user: User, controller: String, action: String): Boolean = Permissions(user.role).contains(s"$controller.*") ||
                                                                       Permissions(user.role).contains(s"$controller.$action")

  def canNot(user: User, controller: String, action: String): Boolean = !can(user, controller, action)
}