import com.stripe.model.WebhookEndpoint
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.HttpRoutes
import com.stripe.Stripe
import org.typelevel.ci._
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model._
import com.stripe.net.Webhook
import scala.util.Try
import fs2.text.utf8
import com.google.gson.JsonSyntaxException

object WebhookRoutes extends Http4sDsl[IO] {

  private def eventHandler(eventType: String) =
    eventType match {

      case "balance.available" => IO.println(eventType)
      // Then define and call a function to handle the event balance.available

      case "charge.captured" => IO.println(eventType)
      // Then define and call a function to handle the event charge.captured

      case "charge.pending" => IO.println(eventType)
      // Then define and call a function to handle the event charge.pending

      case "charge.refunded" => IO.println(eventType)
      // Then define and call a function to handle the event charge.refunded

      case "charge.succeeded" => IO.println(eventType)
      // Then define and call a function to handle the event charge.succeeded

      case "charge.updated" => IO.println(eventType)
      // Then define and call a function to handle the event charge.updated

      case "charge.dispute.created" => IO.println(eventType)
      // Then define and call a function to handle the event charge.dispute.created

      case "coupon.created" => IO.println(eventType)
      // Then define and call a function to handle the event coupon.created

      case "coupon.deleted" => IO.println(eventType)
      // Then define and call a function to handle the event coupon.deleted

      case "invoice.created" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.created

      case "invoice.deleted" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.deleted

      case "invoice.paid" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.paid

      case "invoice.payment_succeeded" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.payment_succeeded

      case "invoice.sent" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.sent

      case "invoice.upcoming" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.upcoming

      case "invoice.updated" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.updated

      case "invoice.voided" => IO.println(eventType)
      // Then define and call a function to handle the event invoice.voided

    }

  // This is your Stripe CLI webhook secret for testing your endpoint locally.
  val endpointSecret = "whsec"

  // The library needs to be configured with your account's secret key.
  // Ensure the key is kept out of any version control system you might be using
  Stripe.apiKey =
    "sk_test_"

  // val webhookHandler= ???
  val stripeRoute = HttpRoutes.of[IO] { case request @ POST -> Root / "webhook" =>
    val payload = request.body.through(utf8.decode).compile.string
    val payload1 = request.bodyText.compile.string
    val sigHeader = request.headers.get(ci"Stripe-Signature").get.head
    val event =
      for {
        body <- payload1
        event <- IO.fromTry(Try(Webhook.constructEvent(body, sigHeader.value, endpointSecret)))
      } yield event

    event
      // .flatTap(ev => IO.println(ev))
      .flatMap(ev => eventHandler(ev.getType()))
      .flatMap(_ => Ok.apply("webhook works just fine") // apply method produces F[Response[G]]
      )
      .handleErrorWith {
        case exception: SignatureVerificationException => BadRequest()
        case exception: JsonSyntaxException            => BadRequest()
      }

  }

}
