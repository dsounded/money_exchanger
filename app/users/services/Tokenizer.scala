package users

object Tokenizer {
  def make(buildingFields: List[Any]) = {
    val currentInMillis = java.lang.System.currentTimeMillis()
    val joinedString = s"${buildingFields.map(_.toString).mkString("")}$currentInMillis}"

    util.Random.shuffle(joinedString.toList).mkString("").take(16)
  }
}
