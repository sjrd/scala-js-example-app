package example

import scala.util.Random

import scala.scalajs.js
import js.annotation.JSExport
import js.Dynamic.{global => g}
import js.JSConverters._

object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    testTupleStackAlloc()
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

  def testForWhileLong(): Unit = {
    benchmarks[Long, Long] {
      123456
    } (
        "for loop" ->
        { n =>
          var r = 0L
          for (i <- 0 until n.toInt)
            r += i.toLong * 2
          r
        },

        "while loop" ->
        { n =>
          var r = 0L
          var i = 0
          val end = n.toInt
          while (i < end) {
            r += i.toLong * 2
            i += 1
          }
          r
        }
    ) { (n, r) =>
      assert(r == (n * (n-1)))
    }
  }

  def testArrayMap(): Unit = {
    benchmarks[js.Array[Int], js.Array[Int]] {
      (1 to 100000).toJSArray
    } (
        "JavaScript Array.prototype.map" ->
        { in =>
          in.asInstanceOf[js.Dynamic]
            .map((x: Int) => x * 2 / 3)
            .map((x: Int) => (x + 4) * 5)
            .asInstanceOf[js.Array[Int]]
        },

        "Scala collections map" ->
        { in =>
          in.map(x => x * 2 / 3)
            .map(x => (x + 4) * 5)
        },

        "Manual" ->
        { in =>
          val out1 = new js.Array[Int]
          var i1 = 0
          while (i1 < in.length) {
            val x = in(i1)
            out1.push(x * 2 / 3)
            i1 += 1
          }

          val out2 = new js.Array[Int]
          var i2 = 0
          while (i2 < out1.length) {
            val x = out1(i2)
            out2.push((x + 4) * 5)
            i2 += 1
          }

          out2
        }
    ) { (input, output) =>
      assert(output.length == input.length)
      for (i <- 0 until output.length)
        assert(output(i) == ((input(i) * 2 / 3) + 4) * 5)
    }
  }

  object MultiInline {
    trait Foo {
      def bar(x: Int): Int = x * 2 + 3
    }
    class A extends Foo
    class B extends Foo
    class C extends Foo
    class D extends Foo
    class E extends Foo
    class F extends Foo
    class G extends Foo
  }

  def testMultiInline(): Unit = {
    import MultiInline._

    // Make sure all the classes exist
    val all = js.Array(new A, new B, new C, new D, new E, new F, new G)

    benchmarks[js.Array[(Foo, Int)], Int] {
      (1 to 1000000).toJSArray.map(x => (all(Random.nextInt(all.length)), x))
    } (
        "multi-inline" ->
        { in =>
          var r = 0
          var i = 0
          val n = in.length
          while (i < n) {
            val x = in(i)
            r ^= x._1.bar(x._2)
            i += 1
          }
          r
        }
    ) { (in, r) =>
      assert(r == in.foldLeft(0)((prev, x) => prev ^ x._1.bar(x._2)))
    }
  }

  def testClosureElim(): Unit = {
    def loop(first: Int, until: Int,
        f: js.Function1[Int, Any]): Unit = {
      var i = first
      while (i < until) {
        f(i)
        i += 1
      }
    }

    def loopAbort(first: Int, until: Int,
        f: js.Function1[Int, Any]): Unit = {
      var i = first
      while (i < until / 2) {
        f(i)
        i += 1
      }
      while (i < until) {
        f(i)
        i += 1
      }
    }

    benchmarks[Int, js.Array[Int]] {
      1000000
    } (
        "Closure elim" ->
        { n =>
          val result = js.Array[Int]()
          loop(0, n, { (i: Int) =>
            result.push(i)
          })
          loop(0, n, { (i: Int) =>
            result.push(n - i)
          })
          result
        },

        "Closure elim aborted" ->
        { n =>
          val result = js.Array[Int]()
          loopAbort(0, n, { (i: Int) =>
            result.push(i)
          })
          loopAbort(0, n, { (i: Int) =>
            result.push(n - i)
          })
          result
        }
    ) { (n, r) =>
      assert(r.sameElements(((0 until n) ++ (n until 0 by -1)).toJSArray))
    }
  }

  def testTupleStackAlloc(): Unit = {
    benchmarks[js.Array[Int], Int] {
      (1 to 10000).toJSArray.map(_ => Random.nextInt())
    } (
        "Tupled" ->
        { in =>
          var r = 0
          var i = 0
          while (i < in.length) {
            val x = in(i)
            val t = (x * 3, x + 4)
            r += t._1 - t._2
            i += 1
          }
          r
        },

        "Manual" ->
        { in =>
          var r = 0
          var i = 0
          while (i < in.length) {
            val x = in(i)
            r += (x * 3) - (x + 4)
            i += 1
          }
          r
        }
    ) { (in, r) =>
      assert(r == in.foldLeft(0)((prev, x) => prev + (x * 3) - (x + 4)))
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

    val samples = new js.Array[Double]

    def fmtTime(time: Double): String = {
      import js.JSNumberOps._
      time.toFixed(2)
    }

    var run = 0
    while (run <= Runs) {
      val initValue = init
      val start = performanceTime()
      val result = body(initValue)
      val end = performanceTime()
      check(initValue, result)

      val elapsed = end - start

      if (run == 0) {
        println(s"WarmUp\t${fmtTime(elapsed)}")
      } else {
        println(s"$run.\t${fmtTime(elapsed)}")
        samples += elapsed
      }

      run += 1
    }

    val (mean, sem) = meanAndSEM(samples)
    println(s"Avg.\t${fmtTime(mean)} ± ${fmtTime(sem)}")
  }

  val performanceTime: js.Function0[Double] = {
    import js.DynamicImplicits._
    if (g.performance && g.performance.now) {
      g.performance.now.asInstanceOf[js.Function0[Double]]
    } else {
      { () =>
        val pair = g.process.hrtime().asInstanceOf[js.Tuple2[Double, Double]]
        (pair._1 * 1000.0) + (pair._2 / 1000000.0)
      }
    }
  }

  def meanAndSEM(samples: js.Array[Double]): (Double, Double) = {
    val n = samples.length
    val mean = samples.sum / n
    val sem = standardErrorOfTheMean(samples, mean)
    (mean, sem)
  }

  def standardErrorOfTheMean(samples: js.Array[Double], mean: Double): Double = {
    val n = samples.length
    Math.sqrt(samples.map(xi => Math.pow(xi - mean, 2)).sum / (n * (n - 1)))
  }

  /** A non-inlined `println` not to pollute the outputs. */
  @noinline
  def println(x: Any): Unit = Predef.println(x)
}
