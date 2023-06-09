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
import org.http4s.multipart.Multipart
import org.http4s.multipart.Multiparts
import org.http4s.multipart.MultipartParser
import org.http4s.multipart.MultipartDecoder
import com.stripe.net.MultipartProcessor
import org.http4s.circe.CirceEntityDecoder._

import org.http4s.circe.CirceEntityEncoder._
import com.stripe.param.PaymentIntentCreateParams

import cats.syntax.all._
import cats.effect.std.Console
import cats.effect.kernel.Async
import cats.data.NonEmptyList
import org.http4s.Header

//import CreatePaymentIntent._
case class WebhookRoutes[F[_]: Async: Console](stripeCredentials: StripeCredentials)
  extends Http4sDsl[F] {

  sealed trait MissingStripeSignatureException extends Throwable

  private def eventHandler(eventType: String) =
    eventType match {

      case "balance.available" => Console[F].println(eventType)
      // Then define and call a function to handle the event balance.available

      case "charge.captured" => Console[F].println(eventType)
      // Then define and call a function to handle the event charge.captured

      case "charge.pending" => Console[F].println(eventType)
      // Then define and call a function to handle the event charge.pending

      case "charge.refunded" => Console[F].println(eventType)
      // Then define and call a function to handle the event charge.refunded

      case "charge.succeeded" => Console[F].println(eventType)
      // Then define and call a function to handle the event charge.succeeded

      case "charge.updated" => Console[F].println(eventType)
      // Then define and call a function to handle the event charge.updated

      case "charge.dispute.created" => Console[F].println(eventType)
      // Then define and call a function to handle the event charge.dispute.created

      case "coupon.created" => Console[F].println(eventType)
      // Then define and call a function to handle the event coupon.created

      case "coupon.deleted" => Console[F].println(eventType)
      // Then define and call a function to handle the event coupon.deleted

      case "invoice.created" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.created

      case "invoice.deleted" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.deleted

      case "invoice.paid" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.paid

      case "invoice.payment_succeeded" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.payment_succeeded

      case "invoice.sent" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.sent

      case "invoice.upcoming" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.upcoming

      case "invoice.updated" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.updated

      case "invoice.voided" => Console[F].println(eventType)
      // Then define and call a function to handle the event invoice.voided

    }

  // This is your Stripe CLI webhook secret for testing your endpoint locally.
  // val endpointSecret = IO("whsec")

  // The library needs to be configured with your account's secret key.
  // Ensure the key is kept out of any version control system you might be using
  // Stripe.apiKey ="sk_test_"

  // val webhookHandler= ???
  val stripeRoute = HttpRoutes.of[F] {
    case request @ POST -> Root / "webhook" =>
      val payload = request.body.through(utf8.decode).compile.string
      val payload1 = request.bodyText.compile.string
      val sigHeader = request.headers.get(ci"Stripe-Signature").get.head

      val sigHeader1: F[NonEmptyList[Header.Raw]] = Async[F].fromOption(
        request.headers.get(ci"Stripe-Signature"),
        new MissingStripeSignatureException {}
      )

      val event1: F[Event] =
        for {
          body <- payload1
          endpointSecret <- EndpointSecret.secret.load[F]
          apiKey <- StripeAPIKey.apiKey.load[F]
          event <- Async[F].fromTry(
            Try(Webhook.constructEvent(body, sigHeader.value, endpointSecret.value))
          )
        } yield event

      val event =
        for {
          body <- payload1
          endpointSecret <- EndpointSecret.secret.load[F]
          header <- sigHeader1
          apiKey <- StripeAPIKey.apiKey.load[F]
          _ <- Async[F].delay(Stripe.apiKey = apiKey.value)
          event <- Async[F].delay(
            Webhook.constructEvent(body, header.head.value, endpointSecret.value)
          )

        } yield event

      event
        // .flatTap(ev => IO.println(ev))
        .flatMap(ev => eventHandler(ev.getType()))
        .flatMap(_ => Ok.apply("webhook works just fine")) // apply method produces F[Response[G]]
        .recoverWith {
          case exception: SignatureVerificationException  => BadRequest()
          case exception: JsonSyntaxException             => BadRequest()
          case exception: MissingStripeSignatureException => BadRequest()

        }

    case request @ POST -> Root / "create-payment-intent" =>
      request
        .as[CreatePaymentIntent]
        .flatMap { payload =>
          val paymentIntentParams = PaymentIntentCreateParams
            .builder()
            .setAmount(23)
            .setCurrency(payload.currency)
            .addPaymentMethodType(payload.paymentMethod.head) // card
            .build()
          val paymentIntent = PaymentIntent.create(paymentIntentParams)

          Ok(CreatePaymentIntentResponse(paymentIntent.getClientSecret()))
        }

  }

}
