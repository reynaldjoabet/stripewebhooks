package configs

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

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
