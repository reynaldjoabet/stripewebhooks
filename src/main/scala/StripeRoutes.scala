import domain._
import domain.JobInfo._
import cats.effect._
import cats.syntax.all._
import core._
//import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.server._
import cats.effect.implicits._
import cats.effect.syntax.all._
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.Logger
//import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import java.util.UUID
import scala.language.implicitConversions
import org.http4s.dsl.Http4sDsl

class StripeRoutes[F[_]: Concurrent: Logger] private (stripe: Stripe[F]) extends Http4sDsl[F] {

  object LimitQueryParam extends OptionalQueryParamDecoderMatcher[Int]("limit")
  object OffsetQueryParam extends OptionalQueryParamDecoderMatcher[Int]("offset")

  ////////// stripe endpoints
  // POST /jobs/promoted { jobInfo } => payment link
  private val promotedJobRoute = AuthedRoutes.of[User, F] {
    case req @ POST -> Root / "promoted" as user =>
      req.req.as[JobInfo].flatMap { jobInfo =>
        for {
          session <- stripe.createCheckoutSession("".toString, user.email)
          resp <- session.map(sesh => Ok(sesh.getUrl)).getOrElse(NotFound())
        } yield resp
      }
  }

  private val promotedWebHook = HttpRoutes.of[F] { case req @ POST -> Root / "webhook" =>
    val stripeSigHeader = req
      .headers
      .get(ci"Stripe-Signature")
      .flatMap(_.toList.headOption)
      .map(_.value)
    stripeSigHeader match {
      case Some(signature) =>
        for {
          payload <- req.bodyText.compile.string
          handled <- stripe.handleWebhookEvent(
            payload,
            signature,
            jobId => Concurrent[F].pure(jobId) /*jobs.activate(UUID.fromString(jobId))  */
          )
          response <-
            if (handled.nonEmpty)
              Ok()
            else
              NoContent()
        } yield response
      case None => Logger[F].info("Got webhook event with no Stripe signature") >> Forbidden()
    }
  }

  val unauthedRoutes = promotedWebHook

  val routes = Router(
    "/jobs" -> (unauthedRoutes)
  )

}

object StripeRoutes {
  def apply[F[_]: Concurrent: Logger](stripe: Stripe[F]) = new StripeRoutes[F](stripe)
}
