import cats.effect.IO

import io.circe.generic.semiauto.deriveEncoder
import io.circe.Encoder

final case class CreatePaymentIntentResponse(clientSecret: String)

object CreatePaymentIntentResponse {

  implicit val createPaymentIntentResponseEncoder: Encoder[CreatePaymentIntentResponse] =
    deriveEncoder[CreatePaymentIntentResponse]

}
