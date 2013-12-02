package example

import scala.collection.mutable

class GameModel(
    val colorNames: Seq[String] = GameModel.DefaultColorNames,
    val maxAttempts: Int = GameModel.DefaultMaxAttempts
) {
  import GameModel._

  val colors = colorNames map Color
  val colorNamed = (colorNames zip colors).toMap

  private[this] val code: Code = (1 to CodeLength) map (
      x => colors(scala.util.Random.nextInt(colors.size)))

  private[this] val colorsInCode = colorsIn(code)

  private val _attempts = new mutable.ArrayBuffer[Attempt]
  _attempts.sizeHint(maxAttempts)

  def attempts = _attempts.toList

  private var _status: GameStatus = GameStatus.InProgress
  def status = _status

  def isInProgress = status == GameStatus.InProgress

  def attemptCode(attemptedCode: Code): Attempt = {
    require(isInProgress)

    val hints = computeHints(attemptedCode)
    val attempt = Attempt(attemptedCode, hints)
    _attempts += attempt

    if (hints.isWinning) _status = GameStatus.Won
    else if (_attempts.size == maxAttempts) _status = GameStatus.Lost

    attempt
  }

  private def computeHints(attemptedCode: Code): Hints = {
    val goods = (code zip attemptedCode) count {
      case (expected, actual) => actual == expected
    }

    val colorsInAttempted = colorsIn(attemptedCode)
    val goodColors =
      for ((color, count) <- colorsInAttempted) yield
        count min colorsInCode(color)
    val goodsAndMisplaced = goodColors.sum

    val misplaced = goodsAndMisplaced - goods

    Hints(goods, misplaced)
  }

  private def colorsIn(code: Code) =
    code.groupBy(x => x).mapValues(_.size).withDefaultValue(0)
}

object GameModel {
  val DefaultMaxAttempts = 15
  val CodeLength = 4

  case class Color(webColor: String)

  type Code = IndexedSeq[Color]

  case class Hints(goods: Int, misplaced: Int) {
    require(goods >= 0 && misplaced >= 0)
    require(goods + misplaced <= CodeLength)

    def isWinning = goods == CodeLength
  }

  val DefaultColorNames =
    Seq("red", "yellow", "green", "blue", "aqua", "orange")

  case class Attempt(code: Code, hints: Hints)

  sealed abstract class GameStatus

  object GameStatus {
    case object InProgress extends GameStatus
    case object Won extends GameStatus
    case object Lost extends GameStatus
  }
}
