import sbt._


val scalaconfig = project
  .in(file("."))
  .settings(
    organization := "com.github.andr83",
    name := "scalaconfig",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.10.6", "2.11.0"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    libraryDependencies ++= Seq(
      Library.typesafeConfig % "provided",
      Library.scalaTest % "test",
      Library.shapeless
    )
  )
