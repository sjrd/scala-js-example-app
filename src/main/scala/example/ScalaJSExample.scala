package example

import scala.scalajs.js
import js.annotation.JSExport
import js.Dynamic.{global => g}

object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    testForWhile()
  }

  def testForWhile(): Unit = {
    benchmarks[Int, Int] {
      123456
    } (
        "for loop" ->
        { n =>
          var r = 0
          for (i <- 0 until n)
            r += i * 2
          r
        },

        "while loop" ->
        { n =>
          var r = 0
          var i = 0
          val end = n
          while (i < end) {
            r += i * 2
            i += 1
          }
          r
        }
    ) { (n, r) =>
      assert(r == (n * (n-1)))
    }
  }

  final val Runs = 10

  /** Really simple benchmarking framework (several implementations). */
  @noinline
  def benchmarks[A, B](init: => A)(implementations: (String, A => B)*)(
      check: (A, B) => Unit): Unit = {
    for ((title, body) <- implementations)
      benchmark(title)(init)(body)(check)
  }

  /** Really simple benchmarking framework (one implementation). */
  @noinline
  def benchmark[A, B](title: String)(init: => A)(body: A => B)(
      check: (A, B) => Unit): Unit = {
    println("")
    println(title)
    println("-" * title.length)
    println("")

    var total = 0.0

    var run = 0
    while (run <= Runs) {
      val initValue = init
      val start = performanceTime()
      val result = body(initValue)
      val end = performanceTime()
      check(initValue, result)

      val elapsed = end - start

      if (run == 0) {
        println(s"Warm.\t$elapsed")
      } else {
        println(s"$run.\t$elapsed")
        total += elapsed
      }

      run += 1
    }

    val average = total / Runs
    println(s"Avg.\t$average")
  }

  val performanceTime: js.Function0[Double] = {
    import js.DynamicImplicits._
    if (g.performance && g.performance.now) {
      g.performance.now.asInstanceOf[js.Function0[Double]]
    } else {
      { () =>
        val pair = g.process.hrtime().asInstanceOf[js.Tuple2[Double, Double]]
        js.Math.round((pair._1 * 1000.0) + (pair._2 / 1000000.0))
      }
    }
  }

  /** A non-inlined `println` not to pollute the outputs. */
  @noinline
  def println(x: Any): Unit = Predef.println(x)
}
