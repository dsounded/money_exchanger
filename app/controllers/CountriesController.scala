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

import io.swagger.annotations._

import actions.AuthorizationAction

@Api(value = "/countries", description = "Countries CRUD", consumes="application/json")
@Singleton
class CountriesController @Inject() extends Controller {
  @ApiOperation(httpMethod = "GET", value = "Get all countries", response = classOf[Country], responseContainer = "List")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success"),
    new ApiResponse(code = 403, message = "Forbidden")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Authorization", value = "Users's auth token", required = true, dataType = "String", paramType = "header")
  ))
  def index = AuthorizationAction.async {
    val countries = Countries.all

    countries.map { country =>
      Ok(Json.obj("countries" -> Json.toJson(country)))
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Get country by ID", response = classOf[Country])
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success"),
    new ApiResponse(code = 404, message = "Not Found")
  ))
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

  @ApiOperation(httpMethod = "POST", value = "Create country", response = classOf[Country])
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Created"),
    new ApiResponse(code = 400, message = "Bad Request")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "country[title]", value = "Country's title", required = true, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "country[abbreviation]", value = "Country's abbreviation", required = true, dataType = "String", paramType = "body")
  ))
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

  @ApiOperation(httpMethod = "DELETE", value = "Destroy country")
  @ApiResponses(Array(
    new ApiResponse(code = 204, message = "No Content"),
    new ApiResponse(code = 404, message = "Not Found")
  ))
  def destroy(id: Long) = Action.async {
    val responder = new Responder[Country]
    responder.destroy(CountryDestroyer.destroy(id)).map { response =>
      response match {
        case Left(status) =>         Status(status)
        case Right((obj, status)) => Status(status)(Json.obj(obj._1 -> Json.toJson(obj._2)))
      }
    }
  }

  @ApiOperation(httpMethod = "PUT/PATCH", value = "Update country")
  @ApiResponses(Array(
    new ApiResponse(code = 204, message = "No Content"),
    new ApiResponse(code = 404, message = "Not Found")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "country[title]", value = "Country's title", required = false, dataType = "String", paramType = "body"),
    new ApiImplicitParam(name = "country[abbreviation]", value = "Country's abbreviation", required = false, dataType = "String", paramType = "body")
  ))
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
