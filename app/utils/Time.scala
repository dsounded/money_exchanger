package utils

import java.sql.Timestamp

object TimeUtil {
  def now = new Timestamp(System.currentTimeMillis)
}
