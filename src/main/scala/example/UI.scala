package example

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.jquery
import jquery.{ JQuery => jQ }

import scala.collection.mutable

class UI(val model: GameModel) {
  import GameModel._

  private val playground = jQ("#playground")
  private val guesses = jQ("<ul>").appendTo(playground)
  private val colorSelectors = ((1 to 4) map (x => makeColorSelector(x)))
  private val guessButton = jQ("<button>").text("Guess")

  {
    val p = jQ("<p>")
    for (selector <- colorSelectors)
      p.append(selector)
    p.append(guessButton)
    p.appendTo(playground)
  }

  private val canvas = jQ("""<canvas width="200" height="520">""").appendTo(playground)
  private val ctx = canvas(0).asInstanceOf[dom.HTMLCanvasElement]
      .getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  //canvas.width(200).height(520)
  drawCanvas()
  for (selector <- colorSelectors)
    selector.change((e: jquery.JQueryEventObject) => drawCanvas())

  def currentCode = {
    val optColors =
      for (selector <- colorSelectors) yield {
        model.colors.filter(_.webColor == selector.`val`().toString)
      }
    val colors = optColors.flatten

    if (colors.size == CodeLength) {
      Some(colors)
    } else {
      None
    }
  }

  guessButton click { () =>
    for (colors <- currentCode) {
      val attempt = model.attemptCode(colors)
      val hints = attempt.hints

      val codeText = colors.map(_.webColor).mkString(" - ")
      val hintsText = s"goods: ${hints.goods} - misplaced: ${hints.misplaced}"
      val text = s"$codeText -- $hintsText"

      jQ("<li>").text(text).appendTo(guesses)

      drawCanvas()

      model.status match {
        case GameStatus.Won => dom.alert("You won!")
        case GameStatus.Lost => dom.alert("You lost!")
        case _ => ()
      }
    }
  }

  private def makeColorSelector(pos: Int) = {
    val select = jQ("<select>")
    for (color <- model.colors)
      select.append(jQ("<option>").`val`(color.webColor).text(color.webColor))
    select
  }

  private def drawCanvas(): Unit = {
    def drawCircle(color: Color, x: js.Number, y: js.Number, dia: js.Number, highlighted: Boolean = false) = {
      def prepPath() = {
        ctx.beginPath()
        val r = dia/2
        ctx.arc(x+r, y+r, r, 0, 2*Math.PI)
      }
      ctx.fillStyle = color.webColor
      prepPath()
      ctx.fill()
      ctx.strokeStyle = (if (highlighted) "white" else "black") + " 2px"
      prepPath()
      ctx.stroke()
    }

    ctx.fillStyle = "tan"
    ctx.fillRect(0, 0, 200, 520)

    def drawCode(code: Code, startRowY: Int) = {
      for ((color, col) <- code.zipWithIndex) {
        drawCircle(color, 20 + 30*col, startRowY, 20)
      }
    }

    for ((attempt, row) <- model.attempts.zipWithIndex) {
      import attempt.hints._

      val startRowY = 20 + 40*row
      drawCode(attempt.code, startRowY)
      for (i <- 0 until CodeLength) {
        val x = 20 + 30*CodeLength + 10 + (i % (CodeLength/2))*10
        val y = startRowY + (i / (CodeLength/2))*10
        val color =
          if (i < goods) Color("black")
          else if (i < goods+misplaced) Color("white")
          else Color("tan")
        drawCircle(color, x, y, 8)
      }
    }

    {
      val startRowY = 20 + 40*model.attempts.size
      currentCode.foreach(drawCode(_, startRowY))
    }
  }
}
