import pureconfig._
import pureconfig.generic.auto._

// case class Port(number: Int)

// sealed trait AuthMethod
// case class Login(username: String, password: String) extends AuthMethod
// case class Token(token: String) extends AuthMethod
// case class PrivateKey(pkFile: java.io.File) extends AuthMethod

// case class ServiceConf(
//   host: String,
//   port: Port,
//   useHttps: Boolean,
//   authMethods: List[AuthMethod]
// )
//to read a config from an application.conf resource and convert it to a case class.
//ConfigSource.default.load[ServiceConf]

//ConfigSource.default is an instance of ConfigSource - a trait representing sources from which we can load configuration data

// The ConfigSource companion object defines many other ready-to-use sources, like:

// ConfigSource.file - reads a config from a file in a file system;
// ConfigSource.resources - reads a config from resources in your classpath or packaged application;
// ConfigSource.url - reads a config from a URL;
// ConfigSource.string - reads a literal config from a string.

case class Conf(name: String, age: Int)

val source = ConfigSource.string("{ name = John, age = 33 }")

// reads a config and loads it into a `Either[ConfigReaderFailures, Conf]`
source.load[Conf]
// res0: ConfigReader.Result[Conf] = Right(Conf("John", 33))

// reads a config and loads it into a `Conf` (throwing if not possible)
source.loadOrThrow[Conf]

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

ConfigSource.default.load[EmailServiceConfig].toString()
// res1: Conf = Conf("John", 33)

import scala.concurrent.duration.FiniteDuration

final case class SecurityConfig(secret: String, jwtExpiryDuration: FiniteDuration)

object SecurityConfig {
  implicit val config: ConfigReader[SecurityConfig] = deriveReader[SecurityConfig]
}

ConfigSource.default.load[SecurityConfig]

final case class Port(number: Int)

final case class KafkaConfig(
  bootstrapServer: String,
  port: Port,
  protocol: String,
  timeout: FiniteDuration
)

val kafkaConf = ConfigSource.default.at("kafka").load[KafkaConfig]
