package example

import scala.scalajs.js
import js.annotation.JSExport
import org.scalajs.dom

class Foo(val x: Int) {
  def setX(value: Int): Unit = ???
}

object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    val foo = new Foo(5)
    println(foo.x)
    foo.setX(10)
    println(foo.x)
  }
}
