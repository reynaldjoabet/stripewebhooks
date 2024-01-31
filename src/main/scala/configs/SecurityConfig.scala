package configs

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import pureconfig.generic

import scala.concurrent.duration.FiniteDuration

final case class SecurityConfig(secret: String, jwtExpiryDuration: FiniteDuration)

object SecurityConfig {
  implicit val config: ConfigReader[SecurityConfig] = deriveReader[SecurityConfig]
}
