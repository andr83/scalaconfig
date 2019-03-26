package com.github.andr83.scalaconfig.instances

import com.github.andr83.scalaconfig.Reader
import com.typesafe.config._
import shapeless._
import shapeless.labelled._

/**
  * @author andr83
  */
trait HReader[L <: HList] {
  def apply(config: Config, defaults: Map[String, Any]): Reader.Result[L]
}

object HReader {
  implicit val hnil: HReader[HNil] = new HReader[HNil] {
    override def apply(config: Config, defaults: Map[String, Any]): Reader.Result[HNil] = Right(HNil)
  }

  implicit def hconsHReader1[K <: Symbol, V, T <: HList](implicit
                                                         witness: Witness.Aux[K],
                                                         hr: Reader[V],
                                                         tr: HReader[T]
                                                        ): HReader[FieldType[K, V] :: T] = new HReader[FieldType[K, V] :: T] {
    override def apply(config: Config, defaults: Map[String, Any]): Reader.Result[FieldType[K, V] :: T] = {
      val key = witness.value.name
      val valueRes: Reader.Result[V] = if (config.hasPath(key)) {
        hr(config, key)
      } else {
        defaults
          .get(key)
          .map(v => Right(v.asInstanceOf[V]))
          .getOrElse(hr(config, key)) //trying to resolve non existing key in reader, e.g. for Option
      }
      val trRes = tr(config, defaults)

      (valueRes, trRes) match {
        case (Right(head), Right(tail)) => Right(field[K](head) :: tail)
        case (Left(errors1), Left(errors2)) => Left(errors1 ++ errors2)
        case (Left(errors), _) => Left(errors)
        case (_, Left(errors)) => Left(errors)
      }
    }
  }
}


trait GenericReader {

  implicit def anyValReader[T <: AnyVal, U](implicit
                                            unwrapped: Unwrapped.Aux[T, U],
                                            reader: Reader[U]
                                           ): Reader[T] = Reader.pureV((config: Config, path: String) => {
    reader(config, path).right.map(unwrapped.wrap)
  })

  implicit def genericReader[H, T <: HList, D <: HList](implicit
                                                        gen: LabelledGeneric.Aux[H, T],
                                                        tr: Lazy[HReader[T]],
                                                        defaults: Default.AsRecord.Aux[H, D],
                                                        defaultMapper: RecordToMap[D]
                                                       ): Reader[H] = new Reader[H] {
    def apply(config: Config, path: String): Reader.Result[H] = {
      tr.value(config.getConfig(path), defaultMapper(defaults())) match {
        case Right(res) => Right(gen.from(res))
        case Left(errors) => Left(errors)
      }
    }
  }

}

object GenericReader extends GenericReader