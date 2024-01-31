package configs

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

final case class PostgresConfig(nThreads: Int, url: String, user: String, pass: String)

object PostgresConfig {
  implicit val config: ConfigReader[PostgresConfig] = deriveReader[PostgresConfig]
}
