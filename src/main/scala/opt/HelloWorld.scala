package opt

import scala.scalajs.js

object HelloWorld extends js.JSApp {
  def main(): Unit = {
    val a = js.Array(6, 7, 3, 10)
    val mul = getMul()
    customForeach(a) { (x: Int) =>
      println(x * mul)
    }
  }

  def customForeach[A](arr: js.Array[A])(f: A => _): Unit = {
    val len = arr.length
    var i = 0
    while (i < len) {
      f(arr(i))
      i += 1
    }
  }

  @noinline
  def getMul(): Int = 2
}
