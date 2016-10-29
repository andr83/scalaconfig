# scalaconfig

[![Build Status](https://travis-ci.org/andr83/scalaconfig.svg?branch=master)](https://travis-ci.org/andr83/scalaconfig)
[![codecov](https://codecov.io/gh/andr83/scalaconfig/branch/master/graph/badge.svg)](https://codecov.io/gh/andr83/scalaconfig)

ScalaConfig is a lightweight wrapper over Typesafe Config library provides scala friendly access. 
It is implemented with type classes pattern and use shapeless for reading case classes.

ScalaConfig adds two metods:

* `as[A](path)` - return value of type A by path in config object
* `as[A]` - convert config object to value of type A

## Supported types

* Primitive (`Int`, `Long`, `Float`, `Double`, `Boolean`)
* `String`, `Symbol`
* Typesafe `Config` and `ConfigValue`
* `FiniteDuration`
* Collections (`List[A]`, `Set[A]`, `Map[String, A]`, `Map[String, AnyRef]`, `Array[A]`, etc. All types with a CanBuildFrom instance are supported)
* `Option[A]`
* Case classes

## Examples

```scala
import com.github.andr83.scalaconfig._

val config: Config = ConfigFactory.load()

val host   = config.as[String]("host")
val port   = config.as[Int]("port")
val path   = config.as[Option[String]]("path")
val users  = config.as[List[String]]("access.users")

case class DbConfig(host: String, port: Int, user: Option[String] = None, passwd: Option[String] = None)

val dbConfig   = config.as[DbConfig]("db")
val dbConfig2  = config.as[DbConfig] // Direct `config` mapping to case class
val dbConfig3  = config.as[Map[String, String]]
val dbConfig3  = config.as[Map[String, AnyRef]]

// Custom reader 
class User(name: String, password: String)

implicit def userReader: Reader[User] = new Reader[User] {
  override def apply(config: Config, path: String): User = {
    val userConfig = config.getConfig(path)
    new User(
      user = userConfig.as[String]("name"),
      password = userConfig.as[String]("password")
    )
  }
}

```

## Usage

### Latest release.

```scala
// for >= Scala 2.10.6, 2.11.x
libraryDependencies += "com.github.andr83" %% "scalaconfig" % "0.2"
```

### Develop branch.

```scala
resolvers += Resolver.sonatypeRepo("snapshots")

// for >= Scala 2.10.6, 2.11.x
libraryDependencies += "com.github.andr83" %% "scalaconfig" % "0.3-SNAPSHOT"
```

## License

MIT License

Copyright (c) 2016 Andrei Tupitcyn
