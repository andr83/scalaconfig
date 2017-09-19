package com.github.andr83.scalaconfig

import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory, ConfigValue, ConfigValueType}
import org.scalatest.{FlatSpec, Inside, Matchers}

import scala.concurrent.duration._

/**
  * @author andr83
  */
class ReaderSpec extends FlatSpec with Matchers with Inside {

  "String value reader" should "read string" in {
    val config = ConfigFactory.parseString(s"stringField = SomeString")

    config.asUnsafe[String]("stringField") should be("SomeString")
  }

  "String value" should "be read as symbol also" in {
    val config = ConfigFactory.parseString(s"stringField = SomeString")

    config.asUnsafe[Symbol]("stringField") should equal('SomeString)
  }

  "Int value reader" should "read int" in {
    val config = ConfigFactory.parseString(s"intField = 42")

    config.asUnsafe[Int]("intField") should be(42)
  }

  "Long value reader" should "read long" in {
    val config = ConfigFactory.parseString(s"longField = 42")

    config.asUnsafe[Long]("longField") should be(42)
  }

  "Float value reader" should "read float" in {
    val config = ConfigFactory.parseString(s"floatField = 42.6")

    config.asUnsafe[Float]("floatField") should be(42.6f)
  }

  "Double value reader" should "read double" in {
    val config = ConfigFactory.parseString(s"doubleField = 42.6")

    config.asUnsafe[Double]("doubleField") should be(42.6)
  }

  "Boolean value reader" should "read boolean" in {
    val config = ConfigFactory.parseString(s"flagField = true")

    config.asUnsafe[Boolean]("flagField") should be(true)
  }

  "Duration value reader" should "read duration values according HOCON spec" in {
    val config = ConfigFactory.parseString(
      """
        |d10s = 10 seconds
        |d1m = 1 minute
        |d12d = 12d
      """.stripMargin)

    config.asUnsafe[FiniteDuration]("d10s") should be(10.seconds)
    config.asUnsafe[FiniteDuration]("d1m") should be(1.minute)
    config.asUnsafe[FiniteDuration]("d12d") should be(12.days)
  }

  "ConfigValue value reader" should "read Typesafe ConfigValue" in {
    val config = ConfigFactory.parseString(s"someField = someValue")

    val configValue = config.asUnsafe[ConfigValue]("someField")
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

    config.asUnsafe[Config]("innerConfig") should be(ConfigFactory.parseString(innerConfigStr))
  }

  "Option value reader" should "wrap existing value in a Some or return a None" in {
    val config = ConfigFactory.parseString(s"stringField = SomeString")

    config.asUnsafe[Option[String]]("stringField") should be(Some("SomeString"))
    config.asUnsafe[Option[String]]("emptyField") should be(None)
  }

  "Traversable reader" should "return any collection which have s CanBuildFrom instance " in {
    val config = ConfigFactory.parseString(
      """
        |stringItems: ["a","b","c"]
        |intItems: [1,2,3]
      """.stripMargin)

    config.asUnsafe[Seq[String]]("stringItems") should be(Seq("a", "b", "c"))
    config.asUnsafe[List[String]]("stringItems") should be(List("a", "b", "c"))
    config.asUnsafe[Array[String]]("stringItems") should be(Array("a", "b", "c"))

    config.asUnsafe[Seq[Option[String]]]("stringItems") should be(Seq(Some("a"), Some("b"), Some("c")))

    config.asUnsafe[Seq[Int]]("intItems") should be(Seq(1, 2, 3))
    config.asUnsafe[List[Int]]("intItems") should be(List(1, 2, 3))
    config.asUnsafe[Array[Int]]("intItems") should be(Array(1, 2, 3))
  }

  "Map reader" should "return Map[String, A]" in {
    val config = ConfigFactory.parseString(
      """
        |mapField = {
        |  key1 = value1
        |  key2 = value2
        |}
      """.stripMargin)

    config.asUnsafe[Map[String, String]]("mapField") should be(Map("key1" -> "value1", "key2" -> "value2"))
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

    config.asUnsafe[Map[String, String]]("parent.mapField") should be(Map("key1" -> "value1", "key2" -> "value2"))
  }

  "Map reader" should "return Map[String, A] also for keys with dots" in {
    val config = ConfigFactory.parseString(
      """
        |mapField = {
        |  "10.11" = value1
        |  "12.13" = value2
        |}
      """.stripMargin)

    config.asUnsafe[Map[String, String]]("mapField") should be(Map("10.11" -> "value1", "12.13" -> "value2"))
  }

  "Config object" should "be directly convert to Map" in {
    val config = ConfigFactory.parseString(
      """
        |{
        |  key1 = value1
        |  key2 = value2
        |}
      """.stripMargin)

    config.asUnsafe[Map[String, String]] should be(Map("key1" -> "value1", "key2" -> "value2"))
  }

  "Config object" should "be able to convert to Map[String, AnyRef]" in {
    val config = ConfigFactory.parseString(
      """
        |{
        |  key1 = value1
        |  key2 = 42
        |}
      """.stripMargin)

    config.asUnsafe[Map[String, AnyRef]] should be(Map("key1" -> "value1", "key2" -> 42))
  }

  "Config object" should "be able to convert java Properties" in {
    val config = ConfigFactory.parseString(
      """
        |{
        |  key1 = value1
        |  key2 = 42
        |}
      """.stripMargin)

    val expected = new Properties()
    expected.put("key1", "value1")
    expected.put("key2", Int.box(42))
    config.asUnsafe[Properties] should be (expected)
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

    config.asUnsafe[Test]("test") should be(Test(key1 = "value1", key2 = 42))
  }

  "Generic reader" should "be able to read nested Case Classes" in {
    val config = ConfigFactory.parseString(
      """
        |test = {
        |  user = {
        |    name = user
        |    password = pswd
        |  }
        |}
      """.stripMargin)

    case class User(name: String, password: String)
    case class Settings(user: User)


    config.asUnsafe[Settings]("test") should be (Settings(User("user", "pswd")))
  }

  "Case class with default Option value to Some(...)" should "be correctly instantiated" in {
    val config = ConfigFactory.parseString(
      """
        |test = {
        |  key1 = value1
        |}
      """.stripMargin)
    case class Test(key1: String, key2: Option[Int] = Some(42) )
    val c = config.asUnsafe[Test]("test")
    c.key1 should be ("value1")
    c.key2 should be (Some(42)) // the default value of the case class should be Some(42) and not None
  }

  "Option value in Case class" should "map to None" in {
    val config = ConfigFactory.parseString(
      """
        |test = {
        |  key1 = value1
        |}
      """.stripMargin)
    case class Test(key1: String, key2: Option[Int])
    val c = config.asUnsafe[Test]("test")
    c.key1 should be ("value1")
    c.key2 should be (None)
  }

  "Custom reader" should "be used" in {
    val config = ConfigFactory.parseString(
      """
        |test = [user, 123]
      """.stripMargin)

    case class Test(key1: String, key2: Int)

    implicit val testReader = Reader.pure[Test]((config: Config, path: String) => {
      val list = config.getList(path)
      Test(list.get(0).unwrapped().asInstanceOf[String], list.get(1).unwrapped().asInstanceOf[Int])
    })

    config.asUnsafe[Test]("test") should be(Test(key1 = "user", key2 = 123))
  }

  "Reader" should "return all errors" in {
    val config = ConfigFactory.parseString(
      """
        |test = {
        |  key1 = value1
        |  key2 = value2
        |}
      """.stripMargin)
    case class Test(key1: Int, key2: Option[Float] = Some(42f))

    val res = config.as[Test]("test")
    inside(res) {
      case Left(errors) => errors should have size 2
    }
  }
}
