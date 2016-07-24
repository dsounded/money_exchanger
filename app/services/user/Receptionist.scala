package services.user

import models.User

import utils.TimeUtil

object Receptionist {
  val DaysActive = 12

  def canEnter(user: User): Boolean = TimeUtil.addDays(user.authTokenCreatedAt, DaysActive).after(TimeUtil.now)
}
