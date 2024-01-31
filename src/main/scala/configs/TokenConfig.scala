package configs

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

final case class TokenConfig(tokenDuration: Long)

object TokenConfig {
  implicit val tokenConfigReader: ConfigReader[TokenConfig] = deriveReader[TokenConfig]
}
