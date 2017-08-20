package com.github.andr83

import com.github.andr83.scalaconfig.instances.{DefaultReader, GenericReader}
import com.typesafe.config.Config

/**
  * @author andr83
  */
package object scalaconfig extends DefaultReader with GenericReader {
  private[scalaconfig] val FakePath: String = "fakePath"

  implicit class ScalaConfig(val config: Config) extends AnyVal {
    def as[A: Reader]: Reader.Result[A] = implicitly[Reader[A]].apply(config.atKey(FakePath), FakePath)

    def as[A: Reader](path: String): Reader.Result[A] = implicitly[Reader[A]].apply(config, path)

    def asUnsafe[A: Reader]: A = as[A].fold(errors=> throw errors.head, identity)

    def asUnsafe[A: Reader](path: String): A = as[A](path).fold(errors=> throw errors.head, identity)
  }

}
