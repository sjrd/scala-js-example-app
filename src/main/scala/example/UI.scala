package example

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.jquery.{ JQuery => jQ }

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

  guessButton click { () =>
    val optColors =
      for (selector <- colorSelectors) yield {
        model.colors.filter(_.webColor == selector.`val`().toString)
      }
    val colors = optColors.flatten

    if (colors.size == CodeLength) {
      val attempt = model.attemptCode(colors)
      val hints = attempt.hints

      val codeText = colors.map(_.webColor).mkString(" - ")
      val hintsText = s"goods: ${hints.goods} - misplaced: ${hints.misplaced}"
      val text = s"$codeText -- $hintsText"

      jQ("<li>").text(text).appendTo(guesses)

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
}
