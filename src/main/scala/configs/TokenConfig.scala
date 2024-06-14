package configs

import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

final case class TokenConfig(tokenDuration: Long)

object TokenConfig {
  implicit val tokenConfigReader: ConfigReader[TokenConfig] = deriveReader[TokenConfig]
}
