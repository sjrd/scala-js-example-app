package example

import scala.scalajs.js
import js.annotation.JSExport
import org.scalajs.dom

object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    if (!js.isUndefined(dom.document)) {
      val paragraph = dom.document.createElement("p")
      paragraph.innerHTML = "<strong>It works!</strong>"
      dom.document.getElementById("playground").appendChild(paragraph)
    } else {
      println("It works!")
    }
  }

  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x
}
