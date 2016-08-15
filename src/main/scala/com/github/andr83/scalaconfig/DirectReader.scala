package com.github.andr83.scalaconfig

import com.typesafe.config.Config

import collection.JavaConverters._

/**
  * @author andr83 
  * created on 15.08.16
  */
trait DirectReader[A] {
  def read(config: Config): A
}

object DirectReader {
  implicit def mapReader[A: Reader] = new DirectReader[Map[String, A]] {
    override def read(config: Config): Map[String, A] = {
      val reader = implicitly[Reader[A]]
      config.root().keySet().asScala.map(k =>
        k -> reader.read(config, k)
      ).toMap
    }
  }
}
