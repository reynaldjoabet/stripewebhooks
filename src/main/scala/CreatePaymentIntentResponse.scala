import io.circe.generic.semiauto.deriveEncoder
import io.circe.Encoder
import cats.effect.IO

final case class CreatePaymentIntentResponse(clientSecret: String)

object CreatePaymentIntentResponse {
  implicit val createPaymentIntentResponseEncoder: Encoder[CreatePaymentIntentResponse] =
    deriveEncoder[CreatePaymentIntentResponse]
}
