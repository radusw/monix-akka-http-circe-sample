import Dependencies._

lazy val root = (project in file("."))
  .settings(commonSettings)

lazy val commonSettings = Seq(
  scalaVersion := "2.12.1",
  version      := "1.0",
  name         := "monix-akka-http-client",
  libraryDependencies ++= dependencies
)
