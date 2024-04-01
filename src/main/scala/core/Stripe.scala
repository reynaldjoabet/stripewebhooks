package core

import cats._
import cats.implicits._
import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import com.stripe.param.checkout.SessionCreateParams
import com.stripe.{Stripe => TheStripe}
import configs.StripeConfig

import org.typelevel.log4cats.Logger
import logging.syntax._
import scala.jdk.OptionConverters._
import scala.util.Try
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams

trait Stripe[F[_]] {
  def createCheckoutSession(jobId: String, userEmail: String): F[Option[Session]]
  def handleWebhookEvent[A](payload: String, signature: String, action: String => F[A])
    : F[Option[A]]

  def createPaymentIntent(amount: Long): F[Option[PaymentIntent]]
}

class LiveStripe[F[_]: MonadThrow: Logger] private (
  key: String,
  price: String,
  successUrl: String,
  cancelUrl: String,
  webhookSecret: String
) extends Stripe[F] {

  // globally set constant :O this Java is really ugly...
  TheStripe.apiKey = key

  override def createCheckoutSession(jobId: String, userEmail: String): F[Option[Session]] =
    SessionCreateParams
      .builder()
      .setMode(SessionCreateParams.Mode.PAYMENT)
      .setInvoiceCreation(
        // one does not code shortly in Java...
        SessionCreateParams.InvoiceCreation.builder().setEnabled(true).build()
      )
      .setPaymentIntentData(
        SessionCreateParams.PaymentIntentData.builder().setReceiptEmail(userEmail).build()
      )
      // used by Stripe to redirect the customer to from the checkout page
      .setSuccessUrl(s"$successUrl/$jobId") // YOUR_DOMAIN+'success.html'
      .setCancelUrl(cancelUrl) // YOUR_DOMAIN+'cancel.html'
      .setCustomerEmail(userEmail)
      .setClientReferenceId(jobId) // from our database
      .addLineItem(
        SessionCreateParams
          .LineItem
          .builder()
          .setQuantity(1L)
          .setPrice(price)
          .build()
      )
      .build()
      .pure[F]
      .map(params => Session.create(params))
      .map(_.some)
      .logError(error => s"Creating checkout session failed: $error")
      .recover { case _ => None }

  override def createPaymentIntent(amount: Long): F[Option[PaymentIntent]] =
    PaymentIntentCreateParams
      .builder()
      .setAmount(amount)
      .setReceiptEmail("hello@gmail.com")
      .setReturnUrl(successUrl)
      .setCurrency("USD")

      // .setCustomer()
      // .putExtraParam()
      // .putMetadata()
      .build()
      .pure[F]
      .map(PaymentIntent.create(_))
      .map(_.some)
      .logError(error => s"Creating checkout session failed: $error")
      .recover { case _ => None }

  override def handleWebhookEvent[A](payload: String, signature: String, action: String => F[A])
    : F[Option[A]] = MonadThrow[F]
    .fromTry(Try(Webhook.constructEvent(payload, signature, webhookSecret)))
    .logError(e => s"Stripe security verification failed - possibly fake attempt")
    .flatMap { event =>
      event.getType() match {
        case "checkout.session.completed" | "checkout.session.async_payment_succeeded" =>
          val session = event
            .getDataObjectDeserializer()
            .getObject()
            .toScala
            .map(_.asInstanceOf[Session])

          session
            .map(_.getPaymentStatus() == "paid")
            .flatMap { paid =>
              if (paid)
                session.map(_.getClientReferenceId())
              else
                None
            }
            // .map(action)
            // .sequence
            .traverse(action) // sequence and map= traverse
            .log(
              {
                case None =>
                  s"Event ${event.getId()} not producing any effect - check Stripe dashboard"
                case Some(v) => s"Event ${event.getId()} fully paid"
              },
              e => s"Webhook action failed: $e"
            )
        case "checkout.session.async_payment_succeededs" =>
          event
            .getDataObjectDeserializer()
            .getObject()
            .toScala
            .map(_.asInstanceOf[Session])
            .map(_.getClientReferenceId())
            .map(action)
            .sequence
        // (None: Option[A]).pure[F]
        case "checkout.session.async_payment_failed" =>
          // Email customer about failed payment
          (None: Option[A]).pure[F]
        // Stripe sends the payment_intent.succeeded event when a payment succeeds, and the payment_intent.payment_failed event when a payment fails.
        // use webhooks to monitor the payment_intent.succeeded event and handle its completion asynchronously
        case "payment_intent.succeeded" =>
          event
            .getDataObjectDeserializer()
            .getObject()
            .toScala
            .map(_.asInstanceOf[PaymentIntent])
            .map(_.getId())
            .map(action)
            .sequence
        case "payment_intent.payment_failed" => ???
        case _                               => (None: Option[A]).pure[F]

      }
    }
    .recover { case _ => None }

}

//  object LiveStripe{
//    def apply[F[_]: MonadThrow: Logger](stripeConfig: StripeConfig): F[LiveStripe[F]] =

//      new LiveStripe[F](key, price, successUrl, cancelUrl, webhookSecret).pure[F]
// }
