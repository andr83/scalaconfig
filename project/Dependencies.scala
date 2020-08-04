import sbt._


object Version {
  val typesafeConfig = "1.4.0"
  val scalaCollectionCompat = "2.1.6"
  val scalaTest = "3.2.0"
  val shapeless = "2.3.3"
  val scalaMacrosParadise = "2.1.1"
}

object Library {
  val typesafeConfig = "com.typesafe" % "config" % Version.typesafeConfig
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  val shapeless = "com.chuusai" %% "shapeless" % Version.shapeless
  val scalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % Version.scalaCollectionCompat
  val scalaMacrosParadise = "org.scalamacros" % "paradise" % Version.scalaMacrosParadise
}