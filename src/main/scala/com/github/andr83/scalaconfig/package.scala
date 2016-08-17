package com.github.andr83

import com.typesafe.config.Config

/**
  * @author andr83
  */
package object scalaconfig {
  private[scalaconfig] val FakePath: String = "fakePath"

  implicit class ScalaConfig(val config: Config) extends AnyVal {
    def as[A: Reader] = implicitly[Reader[A]].apply(config.atKey(FakePath), FakePath)

    def as[A: Reader](path: String) = implicitly[Reader[A]].apply(config, path)
  }

}
