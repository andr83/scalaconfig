package com.github.andr83

import com.typesafe.config.Config

/**
  * @author andr83 
  * created on 15.08.16
  */
package object scalaconfig {
  implicit class ScalaConfig(val config: Config) extends AnyVal{
    def as[A: DirectReader] = implicitly[DirectReader[A]].read(config)
    def as[A: Reader](path: String) = implicitly[Reader[A]].read(config, path)
  }
}
