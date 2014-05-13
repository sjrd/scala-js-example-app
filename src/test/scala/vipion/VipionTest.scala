package vipion

import scala.scalajs.js
import js.Dynamic.{ global => g }
import scala.scalajs.test.JasmineTest

object VipionTest extends JasmineTest {

  import Player._
  import SquareState._

  describe("VipionGame") {

    it("should create a diagonal board") {
      val game = VipionGame.diagonal
      import game._
      expectTrue(board(0, 0) == Empty)
      expectTrue(board(0, 1) == Empty)
      expectTrue(board(1, 1) == Disabled)
      expectTrue(board(1, 2) == Empty)
      expectTrue(board(2, 2) == Disabled)
      expectFalse(done)
      expectFalse(winner.isDefined)
    }
  }

  private def expectTrue(test: Boolean): Unit =
    expect(test).toBeTruthy

  private def expectFalse(test: Boolean): Unit =
    expect(test).toBeFalsy
}
