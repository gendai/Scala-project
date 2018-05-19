import Dependencies._

val scalafx = "org.scalafx" %% "scalafx" % "8.0.144-R12"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.5",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    fork in run := true,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scalafx,
    scalacOptions := Seq("-unchecked", "-deprecation")
  )
