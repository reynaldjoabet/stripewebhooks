package auth

import java.time.{Duration, Instant}
import java.util.UUID

import scala.util.{Failure, Success, Try}

import cats.effect.kernel.Async
import cats.effect.std.Console
import cats.effect.syntax.all._
import cats.effect.Sync
import cats.syntax.all._
import cats.Monad

import com.auth0.jwt.{JWT, JWTVerifier}
import com.auth0.jwt.algorithms.Algorithm
import com.password4j.{Argon2Function, Password}

sealed abstract class AuthService[F[_]: Sync: Console]() {

  def encryptPassword(password: String): F[String] = PasswordHashing.encryptPassword(password)

  def verifyPassword(password: String, passwordHash: String): F[Unit] = PasswordHashing
    .verifyPassword(password, passwordHash)

  def generateJwt(email: String): F[String]  = Jwt.generate(email)
  def verifyJwt(jwtToken: String): F[String] = Jwt.verify(jwtToken)

  private object PasswordHashing {

    final private val MemoryInKib          = 12
    final private val NumberOfIterations   = 20
    final private val LevelOfParallelism   = 2
    final private val LengthOfTheFinalHash = 32
    final private val Type                 = com.password4j.types.Argon2.ID
    final private val Version              = 19

    final private val Argon2: Argon2Function = Argon2Function.getInstance(
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

    def verifyPassword(password: String, passwordHash: String): F[Unit] = Sync[F]
      .delay(Password.check(password, passwordHash).`with`(Argon2))
      .flatTap(res => Console[F].println(res))
      .ifM(ifTrue = Sync[F].delay(()), ifFalse = Sync[F].raiseError(new Exception("Unauthorized")))
    // .flatMap(res=> if(res)Sync[F].delay(()) else Sync[F].raiseError(new Exception("Unauthorized")))

  }

  private object Jwt {

    final private val Issuer    = "SoftwareMill"
    final private val ClaimName = "userEmail"

    final private val algorithm: Algorithm  = Algorithm.HMAC256("hello")
    final private val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(Issuer).build()

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
        case _                   => Sync[F].raiseError(new RuntimeException("Problem with JWT generation!"))
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
