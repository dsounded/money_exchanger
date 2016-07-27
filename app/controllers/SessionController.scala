package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json.Json

import responders.Responder

import models.User

import services.user.Receptionist

import io.swagger.annotations._

@Api(value = "/session", description = "User auth", consumes="application/json")
@Singleton
class SessionController @Inject() extends Controller {
  @ApiOperation(httpMethod = "POST", value = "Create session", response = classOf[User])
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Created"),
    new ApiResponse(code = 404, message = "Bad Request")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "email", value = "User's email", required = true, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "password", value = "User's password", required = true, dataType = "String", paramType = "body")
  ))
  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    val responder = new Responder[User]

    responder.authorize(Receptionist.checkList(request)) map { response =>
      val (body, root, status) = response

      body match {
        case Left(user: User)   => Status(status)(Json.obj(root -> Json.toJson(User.jsonUserWriter.writesForAuth(user))))
        case Right(error)       => Status(status)(Json.obj(root -> Json.toJson(error)))
      }
    }
  }
}
