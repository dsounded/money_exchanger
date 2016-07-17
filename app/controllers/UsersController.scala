package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json.Json

import models.{Users,User}
import services.user.{Creator => UserCreator}
import responders.Responder


@Singleton
class UsersController @Inject() extends Controller {
  def index = Action.async {
    val users = Users.all

    users.map { theUsers =>
      Ok(Json.obj("users" -> Json.toJson(theUsers)))
    }
  }

  def show(id: Long) = Action.async {
    val responder = new Responder[User]
    responder.show(Users.find(id), "user").map { theResponse =>
      val(body, root, status) = theResponse

      body match {
        case Left(recordBody) => Status(status)(Json.obj(root -> Json.toJson(recordBody)))
        case Right(errorBody) => Status(status)(Json.obj(root -> Json.toJson(errorBody)))
      }
    }
  }

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    val responder = new Responder[User]
    responder.create(UserCreator.create(request), "user").flatMap { response =>
      val (body, root, status) = response

      body match {
        case Left(recordBody) => recordBody.map(theRecordBody => Status(status)(Json.obj(root -> Json.toJson(User.jsonUserWriter.writesForCreate(theRecordBody)))))
        case Right(errorBody) => errorBody.map(theErrorBody => Status(status)(Json.obj(root -> Json.toJson(theErrorBody))))
      }
    }
  }
}
