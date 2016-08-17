package com.github.andr83.scalaconfig

import com.typesafe.config.{Config, ConfigValue}

import scala.collection.JavaConverters._
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.util.Try

/**
  * @author andr83
  */
trait DefaultReader {
  implicit val stringReader = new Reader[String] {
    def apply(config: Config, path: String): String = config.getString(path)
  }

  implicit val intReader = new Reader[Int] {
    def apply(config: Config, path: String): Int = config.getInt(path)
  }

  implicit val longReader = new Reader[Long] {
    def apply(config: Config, path: String): Long = config.getLong(path)
  }

  implicit val floatReader = new Reader[Float] {
    def apply(config: Config, path: String): Float = config.getNumber(path).floatValue()
  }

  implicit val doubleReader = new Reader[Double] {
    def apply(config: Config, path: String): Double = config.getDouble(path)
  }

  implicit val booleanReader = new Reader[Boolean] {
    def apply(config: Config, path: String): Boolean = config.getBoolean(path)
  }

  implicit val configValueReader = new Reader[ConfigValue] {
    def apply(config: Config, path: String): ConfigValue = config.getValue(path)
  }

  implicit val configReader = new Reader[Config] {
    def apply(config: Config, path: String) = config.getConfig(path)
  }

  implicit def optReader[A: Reader] = new Reader[Option[A]] {
    def apply(config: Config, path: String): Option[A] = Try(implicitly[Reader[A]].apply(config, path)).toOption
  }

  implicit def traversableReader[A: Reader, C[_]](implicit cbf: CanBuildFrom[Nothing, A, C[A]]) = new Reader[C[A]] {
    def apply(config: Config, path: String): C[A] = {
      val reader = implicitly[Reader[A]]
      val list = config.getList(path).asScala
      val builder = cbf()
      builder.sizeHint(list.size)
      list foreach (item => {
        val entryConfig = item.atPath(FakePath)
        builder += reader(entryConfig, FakePath)
      })
      builder.result()
    }
  }

  implicit def mapReader[A: Reader] = new Reader[Map[String, A]] {
    def apply(config: Config, path: String): Map[String, A] = {
      val reader = implicitly[Reader[A]]
      val obj = config.getConfig(path)
      obj.root().keySet().asScala.map(k =>
        k -> reader(obj, k)
      ).toMap
    }
  }

  implicit val mapStringAnyReader = new Reader[Map[String, AnyRef]] {
    def apply(config: Config, path: String): Map[String, AnyRef] = {
      val obj = config.getConfig(path)
      obj.root().unwrapped().asScala.toMap
    }
  }
}

object DefaultReader extends DefaultReader
