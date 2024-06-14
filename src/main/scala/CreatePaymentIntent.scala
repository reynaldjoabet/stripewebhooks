import cats.effect.IO

import io.circe.generic.semiauto.deriveDecoder
import io.circe.Decoder

final case class CreatePaymentIntent(
  amount: Int,
  paymentMethods: List[StripePaymentMethodType],
  currency: String
)

object CreatePaymentIntent {

  implicit val createPaymentIntentDecoder: Decoder[CreatePaymentIntent] =
    deriveDecoder[CreatePaymentIntent]

//implicit  val createPaymentIntentEntityDecoder:EntityDecoder[IO,CreatePaymentIntent]=jsonOf
}
