package serializers

import scala.util.Try

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._

object CountryRequestSerializer {
  val DefaultValue = ""

  def toModel(request: JsValue) = {
    val title = Try((request \ "title").as[String]).getOrElse(DefaultValue)
    val abbreviation = Try((request \ "abbreviation").as[String]).getOrElse(DefaultValue)

    new Country(0, title, abbreviation, true)
  }
}