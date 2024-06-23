ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

val http4sVersion                      = "0.23.27"

val cirisVersion                       = "3.6.0"
val circeVersion                       = "0.14.8"
val stripeversion                      = "22.31.0"

val catsEffectVersion                  = "3.4.8"
val fs2Version                         = "3.10.2"
val jwtVersion                         = "4.4.0"

val logbackVersion                     = "1.4.14"
val password4jVersion                  = "1.7.3"

val javaMailVersion                    = "1.6.2"
def circe(artifact: String): ModuleID  = "io.circe"   %% s"circe-$artifact"  % circeVersion
def ciris(artifact: String): ModuleID  = "is.cir"     %% artifact            % cirisVersion
def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % http4sVersion

val stripe       = "com.stripe" % "stripe-java"  % stripeversion
val circeCore    = circe("core")
val circeGeneric = circe("generic")
val cireParser   = "io.circe"  %% "circe-parser" % circeVersion

val cirisCore  = ciris("ciris")
val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
val fs2        = "co.fs2"        %% "fs2-core"    % fs2Version

val http4sDsl    = http4s("dsl")
val http4sServer = http4s("ember-server")
val http4sClient = http4s("ember-client")
val http4sCirce  = http4s("circe")

val logback = "ch.qos.logback" % "logback-classic" % logbackVersion

val security = Seq(
  "com.password4j" % "password4j" % password4jVersion,
  "com.auth0"      % "java-jwt"   % jwtVersion
)

val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.17.7"

val pureconfigGeneric = "com.github.pureconfig" %% "pureconfig-generic" % "0.17.7" % Test

val javaMail = "com.sun.mail" % "javax.mail" % javaMailVersion

lazy val root = (project in file(".")).settings(
  name := "StripeWebhooks",
  libraryDependencies ++= Seq(
    cirisCore,
    http4sDsl,
    http4sServer,
    http4sClient,
    http4sCirce,
    circeCore,
    circeGeneric,
    logback,
    catsEffect,
    fs2,
    stripe,
    pureconfig,
    pureconfigGeneric,
    javaMail
  ) ++ security
)

//fork := true

scalacOptions += "-release:17" // ensures the Scala compiler generates bytecode optimized for the Java 17 virtual machine

//We can also set the soruce and target compatibility for the Java compiler by configuring the JavaOptions in build.sbt

// javaOptions ++= Seq(
//   "source","17","target","17"
// )

ThisBuild / semanticdbEnabled := true
ThisBuild / usePipelining     := true
