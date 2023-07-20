package org.fx.application

import utest._

object HelloTests extends TestSuite{
  val tests = Tests{
    test("test1"){
      throw new Exception("test1")
    }
    test("test2"){
      1
    }
    test("test3"){
      val a = List[Byte](1, 2)
      a(1)
    }

    val x = 1
    test("outer1") {
      val y = x + 1

      test("inner1") {
        assert(x == 1, y == 2)
        (x, y)
      }
      test("inner2") {
        val z = y + 1
        assert(z == 3)
      }
    }
    test("outer2") {
      test("inner3") {
        assert(x > 1)
      }
    }
  }

}

object SeparateSetupTests extends TestSuite{
  val tests = Tests{
    var x = 0

    test("outer1"){
      x += 1

      test("inner1"){
        x += 2
        assert(x == 3)
        x
      }

      test("inner2") {
        x += 3
        assert(x == 4) // += 1, += 3
        x
      }

    }

    test("outer2") {
      x += 4
      test("inner3") {
        x += 5
        assert(x == 9) // += 4, += 5
        x
      }
    }
  }
}

object SharedFixturesTests extends TestSuite{
  var x = 0
  val tests = Tests{
    test("outer1"){
      x += 1
      test("inner1"){
        x += 2
        assert(x == 3) // += 1, += 2
        x
      }
      test("inner2"){
        x += 3
        assert(x == 7) // += 1, += 2, += 1, += 3
        x
      }
    }
    test("outer2"){
      x += 4
      test("inner3"){
        x += 5
        assert(x == 16) // += 1, += 2, += 1, += 3, += 4, += 5
        x
      }
    }
  }
}

object NamingTests extends TestSuite{

  val  tests: Tests = Tests {
//    test("test1") - processFileAndCheckOutput("input1.txt", "expected1.txt")
    test("assert"){
      val x = 1L
      val y = 0L
//      Predef.assert(x / y == 10)
//      assert(x / y == 10)
      compileError("true * false")
    }

  }

  def processFileAndCheckOutput(str: String, str1: String): String = {
//   val inputContent = readFile()

//    assert()
    ""
  }


}
