import cats.syntax.all._

import ciris._

final case class StripeCredentials(
  apiKey: Secret[String],
  endpointSecret: Secret[String]
)

object StripeCredentials {

  private[this] val apiKey: ConfigValue[Effect, Secret[String]] =
    env("API_KEY")
      .as[String]
      .default(
        ""
      )
      .secret

  private[this] val endpointSecret: ConfigValue[Effect, Secret[String]] =
    env("ENDPOINT_SECRET")
      .as[String]
      .default(
        ""
      )
      .secret

  val credentials: ConfigValue[Effect, StripeCredentials] = (apiKey, endpointSecret).parMapN(
    StripeCredentials(_, _)
  )

}
