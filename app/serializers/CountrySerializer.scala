package serializers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._

object CountryRequestSerializer {
  def toModel(request: JsValue) = {
    val title = (request \ "title").as[String]
    val abbreviation = (request \ "abbreviation").as[String]

    new Country(0, title, abbreviation, true)
  }
}