import ciris._

final case class StripeAPIKey(value: String)

object StripeAPIKey {
  val apiKey = env("API_KEY").as[String].default("sk_test_").map(str => StripeAPIKey(str))
}
