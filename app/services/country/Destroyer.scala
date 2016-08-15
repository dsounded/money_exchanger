package services.country

import javax.inject.Inject

import models.Countries

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class Destroyer @Inject() (id: Long)(implicit ec: ExecutionContext) {
  def destroy = Countries.destroy(id)
}

object Destroyer {
  def destroy(id: Long)(implicit ec: ExecutionContext): Future[Boolean] = {
    val destroyer = new Destroyer(id)
    destroyer.destroy.map(result => result)
  }

  implicit def intToBoolean(i: Int): Boolean = i != 0
}