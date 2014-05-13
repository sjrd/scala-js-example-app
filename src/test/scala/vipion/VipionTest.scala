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

    it("should declare a winner") {
      val game = VipionGame.diagonal
        .playAt(0, 0) .playAt(3, 0)
        .playAt(0, 1) .playAt(3, 1)
        .playAt(0, 2)
      expectTrue(game.done)
      expectTrue(game.winner == Some(Cross))
    }
  }

  private def expectTrue(test: Boolean): Unit =
    expect(test).toBeTruthy

  private def expectFalse(test: Boolean): Unit =
    expect(test).toBeFalsy
}
