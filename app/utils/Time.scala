package utils

import java.sql.Timestamp

object TimeUtil {
  def now = new Timestamp(System.currentTimeMillis)

  def addDays(startDate: Timestamp, days: Int): Timestamp = {
    new Timestamp(startDate.getTime + days * 3600 * 24 * 1000)
  }
}
