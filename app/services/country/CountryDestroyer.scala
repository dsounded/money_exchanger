package services.country

import models.Countries

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Destroyer(id: Long) {
  def destroy = Countries.destroy(id)
}

object Destroyer {
  def destroy(id: Long): Future[Boolean] = {
    val destroyer = new Destroyer(id)
    destroyer.destroy.map(result => result)
  }

  implicit def intToBoolean(i: Int): Boolean = if (i == 0) false else true
}