import io.circe.generic.semiauto.deriveDecoder
import io.circe.Decoder
import cats.effect.IO

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
