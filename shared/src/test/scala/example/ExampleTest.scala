package example

import utest._

object ExampleTest extends TestSuite {
  val tests = TestSuite {
    "hello" - {
      "world" - {
        val x = 1
        val y = 2
        assert(x != y)
        (x, y)
      }
    }
    "test2" - {
      val a = 1
      val b = 1
      assert(a == b)
    }
  }
}
