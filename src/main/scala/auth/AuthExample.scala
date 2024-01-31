package auth

import cats.effect._
import org.http4s.server._
import org.http4s.HttpRoutes
import cats.data.Kleisli
import cats.data.OptionT
import org.http4s.Response
import org.http4s.Status

object AuthExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      hash <- AuthService.make[IO].encryptPassword("Hellopass")
      _ <- AuthService.make[IO].verifyPassword("Hellopass", hash)
      jwt <- AuthService.make[IO].generateJwt("hello@gmail.com").flatTap(IO.println)
      _ <- IO.println(hash)
    } yield ()).as(ExitCode.Success)

}
