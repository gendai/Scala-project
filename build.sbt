import Dependencies._

val scalafx = "org.scalafx" %% "scalafx" % "8.0.144-R12"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.epita",
      scalaVersion := "2.12.5",
      version      := "1.0.0"
    )),
    name := "ScalaProj",
    fork in run := true,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scalafx,
    scalacOptions := Seq("-unchecked", "-deprecation")
  )
