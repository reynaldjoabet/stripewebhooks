package configs

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

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
