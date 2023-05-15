import ciris._

final case class EndpointSecret(value: String)

object EndpointSecret {
  val secret = env("ENDPOINT_SECRET").as[String].secret
//.map(str=>EndpointSecret(str))
}
