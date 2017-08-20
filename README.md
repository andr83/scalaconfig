# scalaconfig

[![Build Status](https://travis-ci.org/andr83/scalaconfig.svg?branch=master)](https://travis-ci.org/andr83/scalaconfig)
[![codecov](https://codecov.io/gh/andr83/scalaconfig/branch/master/graph/badge.svg)](https://codecov.io/gh/andr83/scalaconfig)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.andr83/scalaconfig_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.andr83/scalaconfig_2.11)

ScalaConfig is a lightweight wrapper over Typesafe Config library provides scala friendly access. 
It is implemented with type classes pattern and use shapeless for reading case classes.

> Current documentation is a actual for 0.4-SNAPSHOT version.

ScalaConfig adds additional metods:

* `as[A](path)` - return Either[Seq[Throwable], A] by path in config object
* `as[A]` - convert config object to Either[Seq[Throwable], A]
* `asUnsafe[A](path)` - return value of type A by path in config object. On fail a first exception will thrown.
* `asUnsafe[A]` - convert config object to value of type A. On fail a first exception will thrown.

## Supported types

* Primitive (`Int`, `Long`, `Float`, `Double`, `Boolean`)
* `String`, `Symbol`
* Typesafe `Config` and `ConfigValue`
* `FiniteDuration`
* `Properties`
* Collections (`List[A]`, `Set[A]`, `Map[String, A]`, `Map[String, AnyRef]`, `Array[A]`, etc. All types with a CanBuildFrom instance are supported)
* `Option[A]`
* Case classes

## Examples

```scala
import com.github.andr83.scalaconfig._

val config: Config = ConfigFactory.load()

val host   = config.asUnsafe[String]("host")
val port   = config.asUnsafe[Int]("port")
val path   = config.asUnsafe[Option[String]]("path")
val users  = config.asUnsafe[List[String]]("access.users")

case class DbConfig(host: String, port: Int, user: Option[String] = None, passwd: Option[String] = None)

val dbConfig: Reader.Result[DbConfig]               = config.as[DbConfig]("db")
val dbConfig2: Reader.Result[DbConfig]              = config.as[DbConfig] // Direct `config` mapping to case class
val dbConfig3: Reader.Result[Map[String, String]]   = config.as[Map[String, String]]
val dbConfig3: Reader.Result[Map[String, AnyRef]]]  = config.as[Map[String, AnyRef]]

// Custom reader 
class User(name: String, password: String)

implicit def userReader: Reader[User] = Reader.pure((config: Config, path: String) => {
    val userConfig = config.getConfig(path)
    new User(
      user = userConfig.asUnsafe[String]("name"),
      password = userConfig.asUnsafe[String]("password")
    )
  }
})

// OR
implicit def userReader: Reader[User] = Reader.pureV((config: Config, path: String) => {
    val userConfig = config.getConfig(path)
    
    val userE = userConfig.as[String]("name")
    val passwordE = userConfig.asUnsafe[String]("password")
    
    // with Cats or Scalaz it can be of course more elegant!
    (userE, passwordE)  match {
      case (Right(user), Right(password)) => Right(new User(user, password))
      case ( Left(errors1), Left(errors2)) => Left(errors1 ++ errors2)
      case (Left(errors), _) => Left(errors)
      case (_, Left(errors)) => Left(errors)
    }
  }
})

```

## Usage

### Latest release.

```scala
// for >= Scala 2.10.6, 2.11.x, 2.12.x
libraryDependencies += "com.github.andr83" %% "scalaconfig" % "0.3"
```

### Develop branch.

```scala
resolvers += Resolver.sonatypeRepo("snapshots")

// for >= Scala 2.10.6, 2.11.x, 2.12.x
libraryDependencies += "com.github.andr83" %% "scalaconfig" % "0.4-SNAPSHOT"
```

## License

MIT License

Copyright (c) 2016 Andrei Tupitcyn
