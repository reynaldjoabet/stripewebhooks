import domain._
import domain.JobInfo._
import cats.effect._
import cats.syntax.all._
import core._
//import cats.implicits._
import com.google.gson.JsonSyntaxException
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.server._
import cats.effect.implicits._
import cats.effect.syntax.all._
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.Logger
import com.stripe.exception.SignatureVerificationException
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
    // create-checkout-session
    case req @ POST -> Root / "promoted" as user =>
      req.req.as[JobInfo].flatMap { jobInfo =>
        for {
          session <- stripe
            .createCheckoutSession("".toString, user.email)
          resp <- session
            .map(sesh => Ok(sesh.getUrl))
            .getOrElse(NotFound()) // can be a 303 redirect
        } yield resp
      }

    case req @ POST -> Root / "create-payment-intent" as user =>
      req.req.bodyText.compile.string.flatMap { amount =>
        for {
          paymentIntent <- stripe
            .createPaymentIntent(amount.toLong)
          resp <- paymentIntent
            .map(paymentInt => Ok(paymentInt.getClientSecret()))
            .getOrElse(NotFound())
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
        (for {
          payload <- req.bodyText.compile.string
          handled <- stripe.handleWebhookEvent(
            payload,
            signature,
            jobId => Concurrent[F].pure(jobId) /*jobs.activate(UUID.fromString(jobId))  */
          )
          response <-
            if (handled.nonEmpty)
              Ok() // back to stripe
            // stripe redirects customer from checkout page to our success page. without this response, Stripe waits for a while before sending our customer back to our site
            // Also if stripe does not get a response back from the webhook handler, those events will be resent over the course of the next few days
            else
              NoContent()
        } yield response).handleErrorWith {
          case _: SignatureVerificationException => BadRequest()
          case _: JsonSyntaxException            => BadRequest()
        }
      case None => Logger[F].info("Got webhook event with no Stripe signature") >> Forbidden()
    }
  }

  val unauthedRoutes = promotedWebHook

  val authedRoutes = promotedJobRoute

  val routes = Router(
    "/jobs" -> (unauthedRoutes),
    "api" -> AuthMiddleware(???).apply(authedRoutes)
  )

}

object StripeRoutes {
  def apply[F[_]: Concurrent: Logger](stripe: Stripe[F]) = new StripeRoutes[F](stripe)
}
