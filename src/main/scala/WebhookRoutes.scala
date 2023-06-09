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
//import CreatePaymentIntent._
object WebhookRoutes extends Http4sDsl[IO] {

  sealed trait MissingStripeSignatureException extends Throwable

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
  // val endpointSecret = IO("whsec")

  // The library needs to be configured with your account's secret key.
  // Ensure the key is kept out of any version control system you might be using
  // Stripe.apiKey ="sk_test_"

  // val webhookHandler= ???
  val stripeRoute = HttpRoutes.of[IO] { 
    case request @ POST -> Root / "webhook" =>
    val payload = request.body.through(utf8.decode).compile.string
    val payload1 = request.bodyText.compile.string
    val sigHeader = request.headers.get(ci"Stripe-Signature").get.head

    val sigHeader1 =
      IO.fromOption(request.headers.get(ci"Stripe-Signature"))(
        new MissingStripeSignatureException {}
      )
    val event1 =
      for {
        body <- payload1
        endpointSecret <- EndpointSecret.secret.load[IO]
        apiKey <- StripeAPIKey.apiKey.load[IO]
        event <- IO.fromTry(
          Try(Webhook.constructEvent(body, sigHeader.value, endpointSecret.value))
        )
      } yield event

    val event =
      for {
        body <- payload1
        endpointSecret <- EndpointSecret.secret.load[IO]
        header <- sigHeader1
        apiKey <- StripeAPIKey.apiKey.load[IO]
        _ <-IO(Stripe.apiKey = apiKey.value)
        event <- IO(Webhook.constructEvent(body, header.head.value, endpointSecret.value))
        
      } yield event

    event
      //.flatTap(ev => IO.println(ev))
      .flatMap(ev => eventHandler(ev.getType()))
      .flatMap(_ => Ok.apply("webhook works just fine")) // apply method produces F[Response[G]]
      .recoverWith {
        case exception: SignatureVerificationException  => BadRequest()
        case exception: JsonSyntaxException             => BadRequest()
        case exception: MissingStripeSignatureException => BadRequest()

      }

      case request @ POST -> Root / "create-payment-intent" =>
        request.as[CreatePaymentIntent]
        .flatMap{payload=>
            
            val paymentIntentParams =PaymentIntentCreateParams
                                            .builder()
                                            .setAmount(23)
                                            .setCurrency(payload.currency)
                                            .addPaymentMethodType(payload.paymentMethod.head)//card
                                            .build()
              val paymentIntent=PaymentIntent.create(paymentIntentParams)                              

            Ok(CreatePaymentIntentResponse(paymentIntent.getClientSecret()))
        }

       
                          

  }

}
