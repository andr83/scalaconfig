import sbt._


object Version {
  val typesafeConfig = "1.2.1"
  val scalaTest = "2.2.+"
  val shapeless = "2.3.1"
  val scalaMacrosParadise = "2.1.0"
}

object Library {
  val typesafeConfig = "com.typesafe" % "config" % Version.typesafeConfig
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  val shapeless = "com.chuusai" %% "shapeless" % Version.shapeless
  val scalaMacrosParadise = "org.scalamacros" % "paradise" % Version.scalaMacrosParadise
}