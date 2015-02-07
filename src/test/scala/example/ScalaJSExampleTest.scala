package example

import utest._

object ScalaJSExampleTest extends TestSuite {

  import ScalaJSExample._

  def tests = TestSuite {
    'ScalaJSExample {
      assert(square(0) == 0)
      assert(square(4) == 16)
      assert(square(-5) == 25)
    }
  }
}
