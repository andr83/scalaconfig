package com.github.andr83.scalaconfig

import com.typesafe.config.Config
import shapeless._
import shapeless.labelled._
import shapeless.ops.record._
import shapeless._ ; import syntax.singleton._ ; import record._

/**
  * @author andr83 
  * created on 16.08.16
  */
trait HReader[L <: HList] {
  def apply(config: Config): L
}

object HReader {
  implicit val hnil: HReader[HNil] = new HReader[HNil] {
    override def apply(config: Config): HNil = HNil
  }

  implicit def hconsHReader1[K <: Symbol, V, T <: HList](implicit
    witness: Witness.Aux[K],
    hr: Reader[V],
    tr: HReader[T]
  ): HReader[FieldType[K, V] :: T] = new HReader[FieldType[K, V] :: T] {
    override def apply(config: Config): FieldType[K, V] :: T = {
      val key = witness.value.name
      val value = hr.read(config, key)
      field[K](value) :: tr(config)
    }
  }
}

trait GenericReader {

  implicit def genericReader[H, T <: HList](implicit
    gen: LabelledGeneric.Aux[H, T],
    tr: HReader[T]
  ): Reader[H] = new Reader[H] {
    override def read(config: Config, path: String): H = {
      gen.from(tr(config.getConfig(path)))
    }
  }

}

object GenericReader extends GenericReader