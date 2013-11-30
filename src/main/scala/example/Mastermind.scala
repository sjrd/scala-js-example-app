package example

object Mastermind {
  def start(): Unit = {
    val model = new GameModel
    new UI(model)
  }
}
