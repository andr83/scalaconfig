package com.github.andr83.scalaconfig

import com.typesafe.config.{Config, ConfigValueType, ConfigValue, ConfigFactory}
import org.scalatest.{Matchers, FlatSpec}
import shapeless.labelled._

/**
  * @author andr83 
  * created on 15.08.16
  */
class ReaderSpec  extends FlatSpec with Matchers {

  "String value reader" should "read string" in {
    val config = ConfigFactory.parseString(s"stringField = SomeString")

    config.as[String]("stringField") should be ("SomeString")
  }

  "Int value reader" should "read int" in {
    val config = ConfigFactory.parseString(s"intField = 42")

    config.as[Int]("intField") should be (42)
  }

  "Long value reader" should "read long" in {
    val config = ConfigFactory.parseString(s"longField = 42")

    config.as[Long]("longField") should be (42)
  }

  "Double value reader" should "read double" in {
    val config = ConfigFactory.parseString(s"doubleField = 42.6")

    config.as[Double]("doubleField") should be (42.6)
  }

  "Boolean value reader" should "read boolean" in {
    val config = ConfigFactory.parseString(s"flagField = true")

    config.as[Boolean]("flagField") should be (true)
  }

  "ConfigValue value reader" should "read Typesafe ConfigValue" in {
    val config = ConfigFactory.parseString(s"someField = someValue")

    val configValue = config.as[ConfigValue]("someField")
    configValue.valueType() should be(ConfigValueType.STRING)
    configValue.unwrapped() should be("someValue")
  }

  "Config value reader" should "read Typesafe Config" in {
    val innerConfigStr =
      """
        |{
        |  field1 = value1
        |  field2 = value2
        |}
      """.stripMargin
    val config = ConfigFactory.parseString(
      s"""
        |innerConfig = $innerConfigStr
      """.stripMargin)

    config.as[Config]("innerConfig") should be(ConfigFactory.parseString(innerConfigStr))
  }

  "Option value reader" should "wrap existing value in a Some or return a None" in {
    val config = ConfigFactory.parseString(s"stringField = SomeString")

    config.as[Option[String]]("stringField") should be (Some("SomeString"))
    config.as[Option[String]]("emptyField") should be (None)
  }

  "Traversable reader" should "return any collection which have s CanBuildFrom instance " in {
    val config = ConfigFactory.parseString(
      """
        |stringItems: ["a","b","c"]
        |intItems: [1,2,3]
      """.stripMargin)

    config.as[Seq[String]]("stringItems") should be (Seq("a", "b", "c"))
    config.as[List[String]]("stringItems") should be (List("a", "b", "c"))
    config.as[Array[String]]("stringItems") should be (Array("a", "b", "c"))

    config.as[Seq[Option[String]]]("stringItems") should be (Seq(Some("a"), Some("b"), Some("c")))

    config.as[Seq[Int]]("intItems") should be (Seq(1, 2, 3))
    config.as[List[Int]]("intItems") should be (List(1, 2, 3))
    config.as[Array[Int]]("intItems") should be (Array(1, 2, 3))
  }

  "Map reader" should "return Map[String, A]" in {
    val config = ConfigFactory.parseString(
      """
        |mapField = {
        |  key1 = value1
        |  key2 = value2
        |}
      """.stripMargin)

    config.as[Map[String, String]]("mapField") should be (Map("key1" -> "value1", "key2" -> "value2"))
  }

  "Map reader" should "return also nested value" in {
    val config = ConfigFactory.parseString(
      """
        |parent {
        |  mapField = {
        |    key1 = value1
        |    key2 = value2
        |  }
        |}
      """.stripMargin)

    config.as[Map[String, String]]("parent.mapField") should be (Map("key1" -> "value1", "key2" -> "value2"))
  }

  "Config object" should "be directly convert to Map" in {
    val config = ConfigFactory.parseString(
      """
        |{
        |  key1 = value1
        |  key2 = value2
        |}
      """.stripMargin)

    config.as[Map[String, String]] should be (Map("key1" -> "value1", "key2" -> "value2"))
  }

  "Config object" should "be able to be converted to Case Class instance" in {
    val config = ConfigFactory.parseString(
      """
        |test = {
        |  key1 = value1
        |  key2 = 42
        |}
      """.stripMargin)
    case class Test(key1: String, key2: Int)

    import GenericReader._
    config.as[Test]("test") should be (Test(key1="value1", key2=42))
  }
}
