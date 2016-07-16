package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json.Json

import models.{Users,User}
import services.country.{Creator => CountryCreator, Destroyer => CountryDestroyer, Updater => CountryUpdater}
import responders.Responder


@Singleton
class UsersController @Inject() extends Controller {
  def index = Action.async {
    val users = Users.all

    users.map { theUsers =>
      Ok(Json.obj("users" -> Json.toJson(theUsers)))
    }
  }
}
