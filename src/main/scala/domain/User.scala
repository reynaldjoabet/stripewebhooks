package domain

final case class User(
  email: String,
  hashedPassword: String,
  firstName: Option[String],
  lastName: Option[String],
  company: Option[String],
  role: String
)
