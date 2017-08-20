package com.github.andr83

import com.github.andr83.scalaconfig.instances.{DefaultReader, GenericReader}
import com.typesafe.config.Config

/**
  * @author andr83
  */
package object scalaconfig extends DefaultReader with GenericReader {
  private[scalaconfig] val FakePath: String = "fakePath"

  implicit class ScalaConfig(val config: Config) extends AnyVal {
    def as[A: Reader]: A = implicitly[Reader[A]].apply(config.atKey(FakePath), FakePath)

    def as[A: Reader](path: String): A = implicitly[Reader[A]].apply(config, path)
  }

}
