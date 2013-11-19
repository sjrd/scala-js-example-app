package example

import scala.scalajs.js
import js.Dynamic.{ global => g }

object Mastermind {
  def start(): Unit = {
    val model = new GameModel
    new UI(model)
  }
}
