package example

import scala.scalajs.js
import js.Dynamic.{ global => g }

import scala.collection.mutable

class UI(val model: GameModel) {
  import GameModel._

  private val document = g.document
  private val jQuery = g.jQuery

  private val playground = jQuery("#playground")
  private val guesses = jQuery("<ul>").appendTo(playground)
  private val colorSelectors = new mutable.ArrayBuffer[js.Dynamic]
  for (x <- 1 to 4)
    colorSelectors += makeColorSelector(x)
  //private val colorSelectors = ((1 to 4).toList map (x => makeColorSelector(x))).toIndexedSeq
  private val guessButton = jQuery("<button>").text("Guess")

  {
    val p = jQuery("<p>")
    for (selector <- colorSelectors)
      p.append(selector)
    p.append(guessButton)
    p.appendTo(playground)
  }

  guessButton click { () =>
    val optColors =
      for (selector <- colorSelectors) yield {
        val selectedValue: String = selector.`val`().asInstanceOf[js.String]
        model.colors.filter(_.webColor == selectedValue)
      }
    val colors = optColors.flatten

    if (colors.size == CodeLength) {
      val attempt = model.attemptCode(colors)
      val hints = attempt.hints

      val codeText = colors.map(_.webColor).mkString(" - ")
      val hintsText = s"goods: ${hints.goods} - misplaced: ${hints.misplaced}"
      val text = s"$codeText -- $hintsText"

      jQuery("<li>").text(text).appendTo(guesses)

      model.status match {
        case GameStatus.Won => g.alert("You won!")
        case GameStatus.Lost => g.alert("You lost!")
        case _ => ()
      }
    }
  }

  private def makeColorSelector(pos: Int) = {
    val select = jQuery("<select>")
    for (color <- model.colors)
      select.append(jQuery("<option>").`val`(color.webColor).text(color.webColor))
    select
  }
}
