package configs

import scala.concurrent.duration.FiniteDuration

import pureconfig.generic
import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

final case class SecurityConfig(secret: String, jwtExpiryDuration: FiniteDuration)

object SecurityConfig {
  implicit val config: ConfigReader[SecurityConfig] = deriveReader[SecurityConfig]
}
