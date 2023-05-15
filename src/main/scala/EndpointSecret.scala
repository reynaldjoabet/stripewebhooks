import ciris._

final case class EndpointSecret(value: String)

object EndpointSecret {
  val secret = env("ENDPOINT_SECRET").as[String].default("whsec_").secret
//.map(str=>EndpointSecret(str))
}
