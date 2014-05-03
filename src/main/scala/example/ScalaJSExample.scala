package example

import scala.scalajs.js
import js.annotation.JSExport
import org.scalajs.dom

@JSExport
object ScalaJSExample {
  @JSExport
  def main(): Unit = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = "<strong>It works!</strong>"
    dom.document.getElementById("playground").appendChild(paragraph)
  }

  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x
}
