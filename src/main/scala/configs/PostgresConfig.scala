package configs

import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

final case class PostgresConfig(nThreads: Int, url: String, user: String, pass: String)

object PostgresConfig {
  implicit val config: ConfigReader[PostgresConfig] = deriveReader[PostgresConfig]
}
