import pureconfig.generic.auto._
import pureconfig._


case class Port(number: Int) 

sealed trait AuthMethod
case class Login(username: String, password: String) extends AuthMethod
case class Token(token: String) extends AuthMethod
case class PrivateKey(pkFile: java.io.File) extends AuthMethod

case class ServiceConf(
  host: String,
  port: Port,
  useHttps: Boolean,
  authMethods: List[AuthMethod]
)
//to read a config from an application.conf resource and convert it to a case class.
ConfigSource.default.load[ServiceConf]

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
// res1: Conf = Conf("John", 33)