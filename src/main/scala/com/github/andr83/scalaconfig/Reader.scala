package com.github.andr83.scalaconfig

import com.typesafe.config.{ConfigValue, Config}

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.util.Try

import collection.JavaConverters._

/**
  * @author andr83
  * created on 15.08.16
  */
trait Reader[A] {
  def read(config: Config, path: String): A
}

object Reader {
  private[this] val FakePath: String = "fakePath"

  implicit val stringReader = new Reader[String] {
    def read(config: Config, path: String): String = config.getString(path)
  }

  implicit val intReader = new Reader[Int] {
    def read(config: Config, path: String): Int = config.getInt(path)
  }

  implicit val longReader = new Reader[Long] {
    def read(config: Config, path: String): Long = config.getLong(path)
  }

  implicit val doubleReader = new Reader[Double] {
    def read(config: Config, path: String): Double = config.getDouble(path)
  }

  implicit val booleanReader = new Reader[Boolean] {
    def read(config: Config, path: String): Boolean = config.getBoolean(path)
  }

  implicit val configValueReader = new Reader[ConfigValue] {
    def read(config: Config, path: String): ConfigValue = config.getValue(path)
  }

  implicit val configReader = new Reader[Config] {
    def read(config: Config, path: String): Config = config.getConfig(path)
  }

  implicit def optReader[A: Reader] = new Reader[Option[A]] {
    def read(config: Config, path: String): Option[A] = Try(implicitly[Reader[A]].read(config, path)).toOption
  }

  implicit def traversableReader[A: Reader, C[_]](implicit cbf: CanBuildFrom[Nothing, A, C[A]]) = new Reader[C[A]] {
    def read(config: Config, path: String): C[A] = {
      val reader = implicitly[Reader[A]]
      val list = config.getList(path).asScala
      val builder = cbf()
      builder.sizeHint(list.size)
      list foreach (item=> {
        val entryConfig = item.atPath(FakePath)
        builder += reader.read(entryConfig, FakePath)
      })
      builder.result()
    }
  }

  implicit def mapReader[A: Reader] = new Reader[Map[String, A]] {
    def read(config: Config, path: String): Map[String, A] = {
      val reader = DirectReader.mapReader[A]
      reader.read(config.getConfig(path))

    }
  }
}
