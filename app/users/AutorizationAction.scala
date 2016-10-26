package users

import play.api.mvc.{ActionBuilder, Result, Request}
import play.api.Logger
import play.api.mvc.Results.Status

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import responders.Responder

import play.api.libs.json.Json

import java.security.cert.X509Certificate

object AuthorizationAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    val header = request.headers.get("Authorization").getOrElse("")

    Logger.info(s"Authorization: $header")

    getUser(header, RequestControllerInfo(request)) flatMap {
      case (user, error) if error isEmpty => block(AuthenticatedRequest(user, request))
      case (_, error)                     => Future.successful(Status(responder.Forbidden)(Json.obj(responder.ErrorRootName -> Json.toJson(error))))
    }
  }

  def getUser(header: String, permissions: (String, String)) = {
    val (controller, action) = permissions

    Users.findByAuthToken(header) map {
      case Some(user) if Permissions.canNot(user, controller, action) => (user, responder.NotAllowed)
      case Some(user) if Receptionist.canEnter(user) => (user, Set.empty)
      case Some(user) => (user, responder.ExpiredTokenError)
      case None => (Users.none, responder.UserNotFound)
    }
  }

  lazy val responder = new Responder

  trait AuthenticatedRequest[+A] extends Request[A]

  object AuthenticatedRequest {
    def apply[A](userObject: Object, requestObject: Request[A]) = new AuthenticatedRequest[A] {
      def id = requestObject.id
      def tags = requestObject.tags
      def uri = requestObject.uri
      def path = requestObject.path
      def method = requestObject.method
      def version = requestObject.version
      def queryString = requestObject.queryString
      def headers = requestObject.headers
      def remoteAddress = requestObject.remoteAddress
      val body = requestObject.body
      val user = userObject
      def clientCertificateChain = Some(Seq.empty[X509Certificate])
      def secure = true
    }
  }

  object RequestControllerInfo {
    val ControllerTupleValue = "ROUTE_CONTROLLER"
    val ActionTupleValue = "ROUTE_ACTION_METHOD"

    def apply[A](request: Request[A]): (String, String) = {
      (cleanControllerName(request.tags.filter { case (key, _) => key == ControllerTupleValue }.head._2),
        request.tags.filter { case (key, _) => key == ActionTupleValue }.head._2)
    }

    private def cleanControllerName(name: String): String = name.replace("controllers.", "").replace("Controller", "")
  }
}
