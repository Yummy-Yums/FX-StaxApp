package org.fx.application

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.ListBuffer

trait Builder extends BeforeAndAfterEach {
  this: Suite =>

  val builder = new StringBuilder

  override def beforeEach() {
    builder.append("ScalaTest is ")
    super.beforeEach() // To be stackable, must call super.beforeEach
  }

  override def afterEach() {
    try {
      super.afterEach() // To be stackable, must call super.afterEach
    }
    finally {
      builder.clear()
    }
  }
}

trait Buffer extends BeforeAndAfterEach {
  this: Suite =>

  val buffer = new ListBuffer[String]

  override def afterEach() {
    try {
      super.afterEach() // To be stackable, must call super.afterEach
    }
    finally {
      buffer.clear()
    }
  }
}

class ExampleSpec extends AnyFlatSpec with Builder with Buffer {

  "Testing" should "be easy" in {
    builder.append("easy!")
    assert(builder.toString === "ScalaTest is easy!")
    assert(buffer.isEmpty)
    buffer += "sweet"
  }

  it should "be fun" in {
    builder.append("fun!")
    assert(builder.toString === "ScalaTest is fun!")
    assert(buffer.isEmpty)
    buffer += "clear"
  }
}
