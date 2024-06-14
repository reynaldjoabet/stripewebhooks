package configs

import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

final case class AppConfig(
  postgresConfig: PostgresConfig,
  emberConfig: EmberConfig,
  securityConfig: SecurityConfig,
  tokenConfig: TokenConfig,
  emailServiceConfig: EmailServiceConfig,
  stripeConfig: StripeConfig
)

object AppConfig {
  implicit val appConfigReader: ConfigReader[AppConfig] = deriveReader[AppConfig]
}
