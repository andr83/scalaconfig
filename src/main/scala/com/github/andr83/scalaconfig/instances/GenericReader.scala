package com.github.andr83.scalaconfig.instances

import com.github.andr83.scalaconfig.Reader
import com.typesafe.config._
import shapeless._
import shapeless.labelled._

/**
  * @author andr83
  */
trait HReader[L <: HList] {
  def apply(config: Config, defaults: Map[String, Any]): L
}

object HReader {
  implicit val hnil: HReader[HNil] = new HReader[HNil] {
    override def apply(config: Config, defaults: Map[String, Any]): HNil = HNil
  }

  implicit def hconsHReader1[K <: Symbol, V, T <: HList](implicit
    witness: Witness.Aux[K],
    hr: Reader[V],
    tr: HReader[T]
  ): HReader[FieldType[K, V] :: T] = new HReader[FieldType[K, V] :: T] {
    override def apply(config: Config, defaults: Map[String, Any]): FieldType[K, V] :: T = {
      val key = witness.value.name
      val value: V = if (config.hasPath(key)) {
        hr(config, key)
      } else {
        defaults
          .getOrElse(key, new ConfigException.BadPath(key, s"Can not build config from generic instance"))
          .asInstanceOf[V]
      }
      field[K](value) :: tr(config, defaults)
    }
  }
}

trait GenericReader {

  implicit def genericReader[H, T <: HList, D <: HList](implicit
    gen: LabelledGeneric.Aux[H, T],
    tr: Lazy[HReader[T]],
    defaults: Default.AsRecord.Aux[H, D],
    defaultMapper: RecordToMap[D]
  ): Reader[H] = new Reader[H] {
    def apply(config: Config, path: String): H = {
      gen.from(tr.value(config.getConfig(path), defaultMapper(defaults())))
    }
  }

}

object GenericReader extends GenericReader