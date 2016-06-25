package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import models.{Countries,Country}
import services.CountryCreator
import responders.Responder
//

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CountriesController @Inject() extends Controller {
  def index = Action.async {
    val countries = Countries.all

    countries.map { country =>
      Ok(Json.obj("countries" -> Json.toJson(country)))
    }
  }

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    val responder = new Responder[Country]
    val (body, root, status) = responder.create(CountryCreator.create(request), "country")

    body match {
      case Left(b) => b.map(b => Status(status)(Json.obj(root -> Json.toJson(b))))
      case Right(eb) => eb.map(b => Status(status)(Json.obj(root -> Json.toJson(b))))
    }
  }
}
