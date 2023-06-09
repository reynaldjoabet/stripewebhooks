import ciris._

final case class EndpointSecret(value: String)

object EndpointSecret {
  val secret = env("ENDPOINT_SECRET").as[String].default("whsec_").map(str => EndpointSecret(str))
}
