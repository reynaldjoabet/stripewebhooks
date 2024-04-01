import scala.concurrent.duration.FiniteDuration
import pureconfig.generic.auto._
import pureconfig._
import configs._

object MainApp extends App {

  final case class Port(number: Int) extends AnyVal

  final case class KafkaConfig(
    bootstrapServer: String,
    port: String,
    protocol: String,
    timeout: FiniteDuration
  )

  val kafkaConf = ConfigSource.default.at("kafka").load[KafkaConfig]
  val kafkaConf2 = ConfigSource.default.load[KafkaConfig]

  val securityConfig = ConfigSource.default.load[SecurityConfig]
  // println(kafkaConf2)
  println(securityConfig)

  // val appConfig=ConfigSource.default.load[AppConfig]

  // println(appConfig)
  // kafkaConf2.foreach(c=>println(c))

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

  val m = ConfigSource.default.load[ServiceConf]

  println(m)
  val ember = ConfigSource.default.load[EmberConfig]

  println(ember)

  val postgres = ConfigSource.default.load[PostgresConfig]
  // println(postgres)

  val stripe = ConfigSource.default.load[StripeConfig]

  println(stripe)

  val email = ConfigSource.default.load[EmailServiceConfig]
  println(email)

  val token = ConfigSource.default.load[TokenConfig]
//println(token)
}
