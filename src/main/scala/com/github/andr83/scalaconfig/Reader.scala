package com.github.andr83.scalaconfig

import com.typesafe.config._

import scala.language.higherKinds

/**
  * @author andr83
  */
trait Reader[A] {
  def apply(config: Config, path: String): A
}

object Reader
