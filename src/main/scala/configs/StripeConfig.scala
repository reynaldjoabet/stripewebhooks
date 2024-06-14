package configs

import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

final case class StripeConfig(
  key: String,
  price: String,
  successUrl: String,
  cancelUrl: String,
  webhookSecret: String
)

object StripeConfig {
  implicit val stripeConfigReader: ConfigReader[StripeConfig] = deriveReader[StripeConfig]
}
