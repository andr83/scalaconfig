package com.github.andr83.scalaconfig

import com.typesafe.config._
import shapeless._
import shapeless.labelled._

/**
  * @author andr83
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
      val value = hr(config, key)
      field[K](value) :: tr(config)
    }
  }
}

trait GenericReader {

  implicit def genericReader[H, T <: HList](implicit
    gen: LabelledGeneric.Aux[H, T],
    tr: Lazy[HReader[T]]
  ): Reader[H] = new Reader[H] {
    def apply(config: Config, path: String): H = {
      gen.from(tr.value(config.getConfig(path)))
    }
  }

}

object GenericReader extends GenericReader