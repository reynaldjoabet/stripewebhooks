import cats.effect._
import cats.effect.ExitCode

import com.comcast.ip4s._
import io.circe
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.defaults.Banner
import org.http4s.server.Server
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.LoggerName

object Main extends IOApp {

//implicit val loggerName=LoggerName("name")
  implicit private val logger: SelfAwareStructuredLogger[cats.effect.IO] = Slf4jLogger.getLogger[IO]

  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] = Logger[F].info(
    s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}"
  )

  override def run(args: List[String]): IO[ExitCode] = StripeCredentials
    .credentials
    .load[IO]
    .flatMap { credentials =>
      EmberServerBuilder
        .default[IO]
        .withHttpApp(WebhookRoutes[IO](credentials).stripeRoute.orNotFound)
        .withPort(port"8081")
        .withHost(host"127.0.0.1")
        // .withLogger(logger)
        // .withTLS()
        // .withHostOption()
        .build.evalTap(showEmberBanner[IO]).useForever
    }
    .as(ExitCode.Success)

}
