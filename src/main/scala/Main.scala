import cats.effect.ExitCode
import cats.effect._
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = EmberServerBuilder
    .default[IO]
    .withHttpApp(WebhookRoutes.stripeRoute.orNotFound)
    .withPort(port"8080")
    .withHost(host"localhost")
    // .withTLS()
    .build
    .useForever
    .as(ExitCode.Success)

}
