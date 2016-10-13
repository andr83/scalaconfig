import sbt._


val scalaconfig = project
  .in(file("."))
  .settings(
    organization := "com.github.andr83",
    name := "scalaconfig",
    version := "0.2-SNAPSHOT",
    scalaVersion := "2.11.8",
    scalacOptions += "-Xlog-implicits",
    crossScalaVersions := Seq("2.10.6", "2.11.0"),
    isSnapshot := version.value.endsWith("-SNAPSHOT"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    libraryDependencies ++= Seq(
      Library.typesafeConfig % "provided",
      Library.scalaTest % "test",
      Library.shapeless,
      compilerPlugin(Library.scalaMacrosParadise cross CrossVersion.full)
    ),
    pomExtra := {
      <url>https://github.com/andr83/scalaconfig</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/andr83/scalaconfig</connection>
        <developerConnection>scm:git:git@github.com/andr83/scalaconfig</developerConnection>
        <url>github.com/andr83/scalaconfig</url>
      </scm>
      <developers>
        <developer>
          <id>andr83</id>
          <name>Andrei Tupitcyn</name>
          <email>andrew.tupitsin@gmail.com</email>
        </developer>
      </developers>
    }
  )
