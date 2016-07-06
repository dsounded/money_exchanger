package services

import models.Countries

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CountryDestroyer(id: Long) {
  def destroy = Countries.destroy(id)
}

object CountryDestroyer {
  def destroy(id: Long): Future[Boolean] = {
    val destroyer = new CountryDestroyer(id)
    destroyer.destroy.map(result => result)
  }

  implicit def intToBoolean(i: Int): Boolean = if (i == 0) false else true
}