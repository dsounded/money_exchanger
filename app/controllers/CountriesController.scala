package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json.Json

import models.{Countries,Country}
import services.country.{Creator => CountryCreator, Destroyer => CountryDestroyer, Updater => CountryUpdater}
import responders.Responder

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

  def show(id: Long) = Action.async {
    val responder = new Responder[Country]
    responder.show(Countries.find(id), "country").map { theResponse =>
      val(body, root, status) = theResponse

      body match {
        case Left(recordBody) => Status(status)(Json.obj(root -> Json.toJson(recordBody)))
        case Right(errorBody) => Status(status)(Json.obj(root -> Json.toJson(errorBody)))
      }
    }
  }

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    val responder = new Responder[Country]
    responder.create(CountryCreator.create(request), "country").flatMap { response =>
      val (body, root, status) = response

      body match {
        case Left(recordBody) => recordBody.map(theRecordBody => Status(status)(Json.obj(root -> Json.toJson(theRecordBody))))
        case Right(errorBody) => errorBody.map(theErrorBody => Status(status)(Json.obj(root -> Json.toJson(theErrorBody))))
      }
    }
  }

  def destroy(id: Long) = Action.async {
    val responder = new Responder[Country]
    responder.destroy(CountryDestroyer.destroy(id)).map { response =>
      response match {
        case Left(status) =>         Status(status)
        case Right((obj, status)) => Status(status)(Json.obj(obj._1 -> Json.toJson(obj._2)))
      }
    }
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { implicit request =>
    val responder = new Responder[Country]
    responder.update(CountryUpdater.update(id, request)) flatMap { response =>
      response map { theResponse =>
        theResponse match {
          case Left(status) => Status(status)
          case Right(responseTuple) => {
            val (errorBody, root, status) = responseTuple

            Status(status)(Json.obj(root -> Json.toJson(errorBody)))
          }
        }
      }
    }
  }
}
