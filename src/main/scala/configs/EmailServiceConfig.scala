package configs

import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

final case class EmailServiceConfig(
  host: String,
  port: Int,
  user: String,
  pass: String,
  frontendUrl: String,
  sender: String
)

object EmailServiceConfig {

  implicit val emailServiceConfigReader: ConfigReader[EmailServiceConfig] =
    deriveReader[EmailServiceConfig]

}
