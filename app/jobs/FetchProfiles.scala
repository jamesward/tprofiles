package jobs

import java.io.File

import play.api._
import play.api.libs.ws.WS
import services.Twitter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Failure}

object FetchProfiles extends App {


  implicit val app = new DefaultApplication(new File("."), FetchProfiles.getClass.getClassLoader, null, Mode.Prod)

  Play.start(app)

  val twitter = Twitter(app)

  val startTime = System.nanoTime()

  val screenName = "_JamesWard"



  val endTime = System.nanoTime()
  Console.println(s"Time taken: ${(endTime - startTime)/1000000000.00}s")

  /*

  val job = for {
    tweets             <- twitter.fetchOriginalTweets("#SalesforceTour")
    users              <- salesforce.createOrUpdateContacts(tweets.map(_.user))
    sentimentTweets    <- twitter.sentimentForTweets(tweets)
    tweetsInSalesforce <- salesforce.upsertTweets(sentimentTweets)
    tasks              <- salesforce.createTasksForTweets(tweetsInSalesforce, 50)
  } yield tasks

  job.onComplete {
    case Success(s) =>
      Play.stop()
    case Failure(e) =>
      Logger.error(e.getMessage)
      Play.stop()
  }
  */

}
