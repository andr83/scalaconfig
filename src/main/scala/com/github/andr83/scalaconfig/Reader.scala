package com.github.andr83.scalaconfig

import com.typesafe.config._

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

/**
  * @author andr83
  */
trait Reader[A] {
  def apply(config: Config, path: String): Reader.Result[A]
}

object Reader {
  type Result[A] = Either[Seq[Throwable], A]

  def pure[A](f: (Config, String)=> A): Reader[A] with Object = new Reader[A] {
    override def apply(config: Config, path: String): Result[A] = Try(f(config, path)) match {
      case Success(a) => Right(a)
      case Failure(e) => Left(Seq(e))
    }
  }

  def pureV[A](f: (Config, String)=> Result[A]): Reader[A] with Object = new Reader[A] {
    override def apply(config: Config, path: String): Result[A] = Try(f(config, path)) match {
      case Success(a) => a
      case Failure(e) => Left(Seq(e))
    }
  }
}
