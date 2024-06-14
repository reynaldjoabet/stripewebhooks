package configs

import scala.reflect.ClassTag

import cats.effect._
import cats.syntax.all._

import pureconfig.error.ConfigReaderException
import pureconfig.ConfigReader
import pureconfig.ConfigSource

object syntax {

  implicit class ConfigSourceOps(val source: ConfigSource) extends AnyVal {

    def loadF[F[_], A: ClassTag: ConfigReader](implicit
      F: Concurrent[F]
    ): F[A] = F
      .pure(source.load[A])
      .flatMap {
        case Left(errors) => F.raiseError[A](ConfigReaderException(errors))
        case Right(value) => F.pure(value)
      }

  }

}
