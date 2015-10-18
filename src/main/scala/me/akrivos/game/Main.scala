package me.akrivos.game

import me.akrivos.game.HandGame.GameMode._
import me.akrivos.game.ui.DefaultConsoleGameUI

import scala.annotation.tailrec

object Main extends App {

  private val gameUI = new DefaultConsoleGameUI(
    RockPaperScissors.Name, RockPaperScissors.Modes, RockPaperScissors.Gestures,
    RockPaperScissors.dummyComputerMoveLogic, RockPaperScissors.apply
  )

  @tailrec
  private def startGame(playing: Boolean): Unit = {
    if (playing) {
      gameUI.showGameHeader()
      gameUI.showGameModes()
      val mode = gameUI.readGameMode()
      val (p1, p2) = mode match {
        case PlayerVsComputer =>
          (gameUI.createConsolePlayer(gameUI.readPlayerName(), gameUI.readPlayerGesture()), gameUI.createComputerPlayer())
        case ComputerVsComputer =>
          (gameUI.createComputerPlayer(), gameUI.createComputerPlayer())
      }
      p1.onPlay(gameUI.showPlayerGesture(p1, _))
      p2.onPlay(gameUI.showPlayerGesture(p2, _))
      val maxScore = gameUI.readMaxScore()
      gameUI.showGameStart(mode)
      val game = gameUI.startNewGame(mode, maxScore)
      game.onRoundWinner(gameUI.showGameRoundWinner)
      val outcome = game.play(p1, p2)
      gameUI.showGameOutcome(outcome)
      val playAgain = gameUI.readPlayAgain()
      startGame(playAgain)
    }
  }

  startGame(playing = true)
}
