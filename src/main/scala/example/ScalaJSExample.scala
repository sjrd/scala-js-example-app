package example

import scala.scalajs.js.annotation.JSExport

import scala.util.parsing.combinator._

/**
 * This trait provides the mathematical operations which the calculator can perform.
 */
trait Maths {
  def add(x: Float, y: Float) = x + y
  def sub(x: Float, y: Float) = x - y
  def mul(x: Float, y: Float) = x * y
  def div(x: Float, y: Float) = if (y > 0) (x / y) else 0.0f
}

/**
 * This class is the complete Reverse Polish parser and calculator
 * JavaTokenParsers is extended in order to use the floatingPointNumber parser
 * Maths is extended to provide the underlying mathematical operations
 */
class ReversePolishCalculator extends JavaTokenParsers with Maths {
  /**
   * Takes an expression, which consists of N repetitions of a term followed by an operator
   * In case you are wondering, the parser combinators used here are as follows:
   *  |   => The alternation combinator, it parses successfully if either the left or right side match
   *  ~   => This combinator forms a sequential combination of it's operands (ex. a~b expects a followed by b)
   *  ~>  => This combinator says "ensure the left operand exists, but don't include it in the result"
   *  <~  => This combinator says "ensure the right operand exists, but don't include it in the result"
   *  ^^  => This combinator says "if parsed successfully, transform the result using the block on the right"
   *  rep => This combinator says "expect zero or more repetitions of X"
   */
  def expr:   Parser[Float] = rep(term ~ operator) ^^ {
    // match a list of term~operator
    case terms =>
      // Each operand will be placed on the stack, and pairs will be popped off for each operation,
      // replacing the pair with the result of the operation. Calculation ends when the final operator
      // is applied to all remaining operands
      var stack  = List.empty[Float]
      // Remember the last operation performed, default to addition
      var lastOp: (Float, Float) => Float = add
      terms.foreach(t =>
        // match on the operator to perform the appropriate calculation
        t match {
          // append the operands to the stack, and reduce the pair at the top using the current operator
          case nums ~ op => lastOp = op; stack = reduce(stack ++ nums, op)
        }
      )
      // Apply the last operation to all remaining operands
      stack.reduceRight((x, y) => lastOp(y, x))
  }
  // A term is N factors
  def term: Parser[List[Float]] = rep(factor)
  // A factor is either a number, or another expression (wrapped in parens), converted to Float
  def factor: Parser[Float] = num | "(" ~> expr <~ ")" ^^ (_.toFloat)
  // Converts a floating point number as a String to Float
  def num: Parser[Float] = floatingPointNumber ^^ (_.toFloat)
  // Parses an operator and converts it to the underlying function it logically maps to
  def operator: Parser[(Float, Float) => Float] = ("*" | "/" | "+" | "-") ^^ {
    case "+" => add
    case "-" => sub
    case "*" => mul
    case "/" => div
  }

  // Reduces a stack of numbers by popping the last pair off the stack, applying op, and pushing the result
  def reduce(nums: List[Float], op: (Float, Float) => Float): List[Float] = {
    // Reversing the list lets us use pattern matching to destructure the list safely
    val result = nums.reverse match {
      // Has at least two numbers at the end
      case x :: y :: xs => xs ++ List(op(y, x))
      // List of only one number
      case List(x)      => List(x)
      // Empty list
      case _            => List.empty[Float]
    }
    result
  }
}

@JSExport
object ScalaJSExample extends ReversePolishCalculator {
  @JSExport
  def main(): Unit = main(Array())

  @JSExport
  def main(args: Array[String]): Unit = {
    val input =
      if (args.length >= 1) args(0)
      else "3 4 - 15 + 12 *"
    println("input: " + input)
    println("result: " + calculate(input))
  }

  // Parse an expression and return the calculated result as a String
  def calculate(expression: String) = parseAll(expr, expression)
}
