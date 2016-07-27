package controllers

import javax.inject._

import play.api._
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json.Json

import models.{User, Users}

import services.user.{Creator => UserCreator}

import responders.Responder

import io.swagger.annotations._

import actions.AuthorizationAction

@Api(value = "/users", description = "Users manipulation", consumes="application/json")
@Singleton
class UsersController @Inject() extends Controller {
  @ApiOperation(httpMethod = "GET", value = "Get all users", response = classOf[User], responseContainer = "List")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success"),
    new ApiResponse(code = 403, message = "Forbidden")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "Users's auth token", required = true, dataType = "String", paramType = "header")
  ))
  def index = AuthorizationAction.async {
    val users = Users.all

    users.map { theUsers =>
      Ok(Json.obj("users" -> Json.toJson(theUsers)))
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Get user by ID", response = classOf[User])
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success"),
    new ApiResponse(code = 404, message = "Not Found"),
    new ApiResponse(code = 403, message = "Forbidden")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "Users's auth token", required = true, dataType = "String", paramType = "header")
  ))
  def show(id: Long) = AuthorizationAction.async {
    val responder = new Responder[User]
    responder.show(Users.find(id), "user").map { theResponse =>
      val(body, root, status) = theResponse

      body match {
        case Left(recordBody) => Status(status)(Json.obj(root -> Json.toJson(recordBody)))
        case Right(errorBody) => Status(status)(Json.obj(root -> Json.toJson(errorBody)))
      }
    }
  }

  @ApiOperation(httpMethod = "POST", value = "Create user", response = classOf[User])
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Created"),
    new ApiResponse(code = 404, message = "Bad Request")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "user[email]", value = "User's email", required = true, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "user[password]", value = "User's password", required = true, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "user[firstName]", value = "User's first name", required = false, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "user[lastName]", value = "User's last name", required = false, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "user[role]", value = "User's role", required = false, dataType = "String", paramType = "body",
                         allowableValues = "User, Moderator, User"),
    new ApiImplicitParam(name = "user[cityId]", value = "User's city ID", required = false, dataType = "Int", paramType = "body"),
    new ApiImplicitParam(name = "user[phone]", value = "User's phone", required = false, dataType = "String", paramType = "body")
  ))
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
