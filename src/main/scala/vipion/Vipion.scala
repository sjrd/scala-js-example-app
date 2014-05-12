package vipion

import scala.util.Random

import scala.scalajs.js
import js.annotation.JSExport
import org.scalajs.dom
import dom.extensions._

sealed abstract class Player {
  def opponent: Player
}
object Player {
  case object Cross extends Player {
    def opponent = Circle
  }
  case object Circle extends Player {
    def opponent = Cross
  }
}

sealed abstract class SquareState
object SquareState {
  case object Empty extends SquareState
  case object Disabled extends SquareState
  case class Mark(player: Player) extends SquareState
}

case class VipionGame private (
    private val _board: Seq[Seq[SquareState]],
    val currentPlayer: Player
) {
  import VipionGame._

  lazy val winner: Option[Player] = {
    (for {
      x <- Iterable.range(0, 4)
      y <- Iterable.range(0, 4)
      s = board(x, y)
      p <- (s match {
        case SquareState.Mark(p) => List(p)
        case _                   => Nil
      })
      (dirX, dirY) <- Seq((1, 0), (1, 1), (0, 1), (-1, 1))
      if inBounds(x+2*dirX, y+2*dirY)
      if (board(x+dirX, y+dirY) == s) && (board(x+2*dirX, y+2*dirY) == s)
    } yield p).headOption
  }

  def done = winner.isDefined || isFull

  def isFull = _board.forall(_.forall(_ != SquareState.Empty))

  def board(x: Int, y: Int): SquareState = _board(x)(y)

  private def withSquare(x: Int, y: Int, s: SquareState): VipionGame =
    new VipionGame(_board.updated(x, _board(x).updated(y, s)), currentPlayer)

  private def withNextPlayer: VipionGame =
    new VipionGame(_board, currentPlayer.opponent)

  def playAt(x: Int, y: Int): VipionGame = {
    if (inBounds(x, y) && board(x, y) == SquareState.Empty)
      this.withSquare(x, y, SquareState.Mark(currentPlayer)).withNextPlayer
    else
      this
  }

  override def toString(): String = {
    val lines = for (y <- 0 until Size) yield {
      val chars = for (x <- 0 until Size) yield {
        board(x, y) match {
          case SquareState.Empty               => "."
          case SquareState.Disabled            => "*"
          case SquareState.Mark(Player.Cross)  => "X"
          case SquareState.Mark(Player.Circle) => "O"
        }
      }
      chars.mkString
    }
    lines.mkString("\n", "\n", "\n") + s"winner: $winner\n"
  }
}
object VipionGame {
  val Size = 4

  def inBounds(x: Int, y: Int): Boolean =
    x >= 0 && x < Size && y >= 0 && y < Size

  private val Empty =
    new VipionGame(Seq.fill(4, 4)(SquareState.Empty), Player.Cross)

  def apply(disabled: (Int, Int)*): VipionGame = {
    disabled.foldLeft(Empty) { case (prev, (x, y)) =>
      prev.withSquare(x, y, SquareState.Disabled)
    }
  }

  def diagonal: VipionGame = apply((1, 1), (2, 2))
}

object Vipion extends js.JSApp {
  private var computerPlayer: Option[Player] = Some(Player.Cross)
  private var computerMaxDepth: Int = 3

  private var game: VipionGame = VipionGame.diagonal

  import VipionGame.{Size => BoardSize}
  val SquareSize = 50
  val BorderWidth = 2
  val MarkWidth = 4

  def isComputerTurn = computerPlayer.exists(game.currentPlayer == _)

  val canvas = dom.document.createElement("canvas").asInstanceOf[dom.HTMLCanvasElement]
  canvas.width = BoardSize*SquareSize+2
  canvas.height = canvas.width
  val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  dom.document.getElementById("playground").appendChild(canvas)

  def main(): Unit = {
    canvas.onmousedown = { (e: dom.MouseEvent) =>
      if (game.done) {
        startGame()
      } else if (!isComputerTurn) {
        val x = e.asInstanceOf[js.Dynamic].offsetX.asInstanceOf[js.Number].toInt / SquareSize
        val y = e.asInstanceOf[js.Dynamic].offsetY.asInstanceOf[js.Number].toInt / SquareSize
        playAt(x, y)
      }
    }

    startGame()
  }

  def startGame(): Unit = {
    game = VipionGame.diagonal
    drawBoard()
    if (isComputerTurn)
      dom.setTimeout(computerPlay _, 10)
  }

  def playAt(x: Int, y: Int): Unit = {
    game = game.playAt(x, y)
    drawBoard()
    if (game.done) {
      game.winner match {
        case Some(winner) => dom.alert(s"$winner won!")
        case None         => dom.alert("Draw")
      }
    } else if (isComputerTurn) {
      dom.setTimeout(computerPlay _, 10)
    }
  }

  def drawBoard(): Unit = {
    renderer.fillStyle = "white"
    renderer.fillRect(0, 0, BoardSize*SquareSize, BoardSize*SquareSize)

    for (x <- 0 until BoardSize; y <- 0 until BoardSize) {
      val left = x*SquareSize
      val top = y*SquareSize
      val right = left + SquareSize
      val bottom = top + SquareSize
      game.board(x, y) match {
        case SquareState.Empty =>
        case SquareState.Disabled =>
          renderer.fillStyle = "gray"
          renderer.fillRect(left, top, SquareSize, SquareSize)
        case SquareState.Mark(Player.Cross) =>
          renderer.strokeStyle = "blue"
          renderer.lineWidth = MarkWidth
          renderer.strokePath((left+1, top+1), (right-1, bottom-1))
          renderer.strokePath((left+1, bottom-1), (right-1, top+1))
        case SquareState.Mark(Player.Circle) =>
          renderer.strokeStyle = "red"
          renderer.lineWidth = MarkWidth
          renderer.strokeCircle(left+SquareSize/2, top+SquareSize/2, SquareSize/2-2)
      }
    }

    renderer.strokeStyle = "black"
    renderer.lineWidth = BorderWidth
    for (i <- 0 to BoardSize) {
      renderer.strokePath((0, i*SquareSize), (BoardSize*SquareSize, i*SquareSize))
      renderer.strokePath((i*SquareSize, 0), (i*SquareSize, BoardSize*SquareSize))
    }
  }

  def computerPlay(): Unit = {
    assert(isComputerTurn && !game.done)

    case class BestMove(x: Int, y: Int, game: VipionGame, depth: Int)

    val cache = scala.collection.mutable.Map.empty[VipionGame, BestMove]

    def bestMove(game: VipionGame, depth: Int): BestMove = cache.getOrElseUpdate(game, {
      assert(!game.done)
      val nextDepth = depth+1
      val moves = for {
        x <- 0 until BoardSize
        y <- 0 until BoardSize
        if game.board(x, y) == SquareState.Empty
      } yield {
        val nextGame = game.playAt(x, y)
        if (nextGame.done || depth >= computerMaxDepth) {
          BestMove(x, y, nextGame, depth)
        } else {
          bestMove(nextGame, nextDepth).copy(x = x, y = y)
        }
      }
      val me = game.currentPlayer
      val opponent = me.opponent
      val shuffledMoves =
        if (depth == 0) Random.shuffle(moves)
        else moves
      shuffledMoves.min(Ordering.fromLessThan[BestMove] { (lhs, rhs) =>
        // Return true iff lhs is better than rhs
        (lhs.game.winner, rhs.game.winner) match {
          case (Some(`me`), Some(`me`)) =>
            // win as quickly as possible
            lhs.depth < rhs.depth
          case (a, b) if a == b =>
            // delay loss and draw as much as possible
            lhs.depth > rhs.depth
          case (Some(`me`), _)       => true
          case (_, Some(`opponent`)) => true
          case _                     => false
        }
      })
    })

    val BestMove(x, y, _, _) = bestMove(game, 0)
    assert(game.board(x, y) == SquareState.Empty)
    playAt(x, y)
  }

  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x
}
