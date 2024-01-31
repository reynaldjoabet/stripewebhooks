package auth

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.{JWT, JWTVerifier}
import com.password4j.{Argon2Function, Password}
import cats.syntax.all._
import cats.effect.syntax.all._

import java.time.{Duration, Instant}
import java.util.UUID
import scala.util.{Failure, Success, Try}
import cats.effect.kernel.Async
import cats.Monad
import cats.effect.Sync
import cats.effect.std.Console

sealed abstract class AuthService[F[_]: Sync: Console]() {
  def encryptPassword(password: String): F[String] = PasswordHashing.encryptPassword(password)
  def verifyPassword(password: String, passwordHash: String): F[Unit] = PasswordHashing
    .verifyPassword(password, passwordHash)
  def generateJwt(email: String): F[String] = Jwt.generate(email)
  def verifyJwt(jwtToken: String): F[String] = Jwt.verify(jwtToken)

  private object PasswordHashing {
    private final val MemoryInKib = 12
    private final val NumberOfIterations = 20
    private final val LevelOfParallelism = 2
    private final val LengthOfTheFinalHash = 32
    private final val Type = com.password4j.types.Argon2.ID
    private final val Version = 19

    private final val Argon2: Argon2Function = Argon2Function.getInstance(
      MemoryInKib,
      NumberOfIterations,
      LevelOfParallelism,
      LengthOfTheFinalHash,
      Type,
      Version
    )

    def encryptPassword(password: String): F[String] = Sync[F].delay(
      Password.hash(password).`with`(Argon2).getResult
    )

    def verifyPassword(password: String, passwordHash: String): F[Unit] = (Sync[F]
      .delay(Password.check(password, passwordHash) `with` Argon2))
      .flatTap(res => Console[F].println(res))
      .ifM(ifTrue = Sync[F].delay(()), ifFalse = Sync[F].raiseError(new Exception("Unauthorized")))
    // .flatMap(res=> if(res)Sync[F].delay(()) else Sync[F].raiseError(new Exception("Unauthorized")))

  }

  private object Jwt {
    private final val Issuer = "SoftwareMill"
    private final val ClaimName = "userEmail"

    private final val algorithm: Algorithm = Algorithm.HMAC256("hello")
    private final val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(Issuer).build()

    def generate(email: String): F[String] = {
      val now: Instant = Instant.now()
      Try(
        JWT
          .create()
          .withIssuer(Issuer)
          .withClaim(ClaimName, email)
          .withIssuedAt(now)
          .withExpiresAt(now.plus(Duration.ofHours(1)))
          .withJWTId(UUID.randomUUID().toString)
          .sign(algorithm)
      ) match {
        case Success(createdJwt) => Sync[F].delay(createdJwt)
        case _ => Sync[F].raiseError(new RuntimeException("Problem with JWT generation!"))
      }
    }

    def verify(jwtToken: String): F[String] =
      Try(verifier.verify(jwtToken)) match {
        case Success(decodedJwt) => Sync[F].delay(decodedJwt.getClaim(ClaimName).asString())
        case _                   => Sync[F].raiseError(new Exception("Invalid token!"))
      }

  }

}

object AuthService {
  def make[F[_]: Sync: Console]: AuthService[F] = new AuthService[F] {}
}
