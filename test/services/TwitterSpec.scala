package services

import org.scalatestplus.play._
import org.scalatest.matchers.BeMatcher._
import play.api.libs.json.JsArray
import play.api.test.Helpers._

class TwitterSpec extends PlaySpec with OneAppPerSuite {

  "Twitter" must {
    "fetch some followers" in {
      val someFollowers = await(Twitter(app).followers(Right("_jamesward")))
      (someFollowers \ "ids").as[JsArray].value.size must be > 0
    }
    "fetch all followers for _jamesward" in {
      val allFollowers = await(Twitter(app).allFollowers(Right("_jamesward")))
      allFollowers.size must be > 9000
    }
    "fetch all followers for netflix" in {
      val allFollowers = await(Twitter(app).allFollowers(Right("netflix")))
      allFollowers.size must be > 1000000000
    }
  }

}