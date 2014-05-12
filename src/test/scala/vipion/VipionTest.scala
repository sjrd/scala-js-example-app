package vipion

import scala.scalajs.js
import js.Dynamic.{ global => g }
import scala.scalajs.test.JasmineTest

object VipionTest extends JasmineTest {

  describe("Vipion") {

    it("should implement square()") {
      import Vipion._

      expect(square(0)).toBe(0)
      expect(square(4)).toBe(16)
      expect(square(-5)).toBe(25)
    }
  }
}
