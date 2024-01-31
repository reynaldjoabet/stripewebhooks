package domain

import io.circe.generic.semiauto.deriveCodec
import org.http4s.circe.jsonOf
import io.circe.Codec
import cats.effect.Concurrent

case class JobInfo(
  company: String,
  title: String,
  description: String,
  externalUrl: String,
  remote: Boolean,
  location: String,
  salaryLo: Option[Int],
  salaryHi: Option[Int],
  currency: Option[String],
  country: Option[String],
  tags: Option[List[String]],
  image: Option[String],
  seniority: Option[String],
  other: Option[String]
)

object JobInfo {
  implicit val jobInfoCodec: Codec[domain.JobInfo] = deriveCodec[JobInfo]
  // def EntityDecoder[F[_]:Concurrent] = jsonOf[F,JobInfo]
}
