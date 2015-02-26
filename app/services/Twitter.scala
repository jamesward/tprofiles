package services

import play.api.Application
import play.api.http.Status
import play.api.libs.json.{JsArray, JsValue}
import play.api.libs.ws.{WSAuthScheme, WS}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Twitter(implicit app: Application) {

  val consumerKey = app.configuration.getString("twitter.consumer.key")
  val consumerSecret = app.configuration.getString("twitter.consumer.secret")

  lazy val bearerTokenFuture: Future[String] = {
    require(consumerKey.isDefined)
    require(consumerSecret.isDefined)

    WS.url("https://api.twitter.com/oauth2/token")
      .withAuth(consumerKey.get, consumerSecret.get, WSAuthScheme.BASIC)
      .post(Map("grant_type" ->  Seq("client_credentials")))
      .withFilter(response => (response.json \ "token_type").asOpt[String] == Some("bearer"))
      .map(response => (response.json \ "access_token").as[String])
  }

  def followers(userIdOrScreenName: Either[Long, String], cursor: Option[Long] = None, count: Option[Int] = None): Future[JsValue] = {
    bearerTokenFuture.flatMap { bearerToken =>
      val userIdOrScreenNameParam = userIdOrScreenName.fold("user_id" -> _.toString, "screen_name" -> _)

      WS.url("https://api.twitter.com/1.1/followers/ids.json").withQueryString(
        "cursor" -> cursor.getOrElse(-1).toString,
        "count" -> count.getOrElse(5000).toString,
        userIdOrScreenNameParam
      ).withHeaders("Authorization" -> s"Bearer $bearerToken").get().flatMap { response =>
        response.status match {
          case Status.OK =>
            Future.successful(response.json)
          case _ =>
            Future.failed(new IllegalStateException(response.json.toString()))
        }
      }
    }
  }

  def allFollowers(userIdOrScreenName: Either[Long, String]): Future[Seq[Long]] = {
    def maybeNext(response: JsValue): Future[Seq[Long]] = {
      val ids = (response \ "ids").as[Seq[Long]]
      val nextCursor = (response \ "next_cursor").as[Long]

      if (nextCursor == 0) {
        Future.successful(ids)
      }
      else {
        followers(userIdOrScreenName, Some(nextCursor)).flatMap(maybeNext).map(ids ++: _)
      }
    }

    followers(userIdOrScreenName).flatMap(maybeNext)
  }

  /*
  def profiles(userIdsOrScreenNames: Either[Seq[Long], Seq[String]]): Future[JsArray] = {


  }
  */

}

object Twitter {
  def apply(implicit app: Application) = new Twitter()
}