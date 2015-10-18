package me.akrivos.game.ui

import me.akrivos.game.HandGame
import me.akrivos.game.HandGame._

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Success, Try}

trait ConsoleGameUI {
  def startNewGame(mode: GameMode, maxScore: Score): HandGame
  def createConsolePlayer(name: String, pickMove: => Gesture): Player
  def createComputerPlayer(): Player
  def showGameHeader(): Unit
  def showGameModes(): Unit
  def showGameStart(game: GameMode): Unit
  def showGameRoundWinner(round: Option[(Player, Score, Score)]): Unit
  def showGameOutcome(outcome: Outcome): Unit
  def showPlayerGesture(player: Player, gesture: Gesture): Unit
  def readGameMode(): GameMode
  def readMaxScore(): Score
  def readPlayerName(): String
  def readPlayerGesture(): Gesture
  def readPlayAgain(): Boolean
}

class DefaultConsoleGameUI(
  gameName: String, gameModes: Set[GameMode], gameGestures: Set[Gesture],
  computerMoveLogic: => Gesture, newGame: (GameMode, Score) => HandGame
) extends ConsoleGameUI {

  private val modeMap = gameModes.zipWithIndex.map { case (m, i) => (i + 1) -> m }.toMap

  override def startNewGame(mode: GameMode, maxScore: Score) = newGame(mode, maxScore)

  def createConsolePlayer(name: String, pickMove: => Gesture) = Player(name, pickMove)

  def createComputerPlayer() = Player(computerMoveLogic)

  override def showGameHeader() = {
    val stars = Seq.fill(gameName.length + 8)("*").mkString
    println(stars)
    println(s"**  $gameName  **")
    println(stars)
  }

  override def showGameModes() = {
    println("Game modes:")
    modeMap.foreach { case (i, m) =>
      println(s"  $i. ${m.name}")
    }
  }

  override def showGameStart(mode: GameMode) = {
    println()
    println(mode.name)
    println("Starting game...")
    println()
  }

  def showGameRoundWinner(round: Option[(Player, Score, Score)]) = {
    round match {
      case Some((winner, score1, score2)) =>
        println(s"*** ${winner.name} won this round!")
        println(s"*** Score is now $score1 - $score2")
      case None =>
        println("*** It's a draw!")
    }
    println()
  }

  override def showGameOutcome(outcome: Outcome) = {
    val Outcome(winner, winScore, loser, loseScore) = outcome
    val stars = Seq.fill(40)("*").mkString
    println(stars)
    println(s"  ${winner.name} won ${loser.name}")
    println(s"  The final score was $winScore - $loseScore")
    println(stars)
    println()
  }

  override def showPlayerGesture(player: Player, gesture: Gesture) = {
    println(s"${player.name} picked ${gesture.name}")
  }

  @tailrec
  final override def readGameMode() = {
    print(s"Mode [${modeMap.keys.mkString(",")}]: ")
    Try(StdIn.readInt()) match {
      case Success(m) if modeMap.contains(m) => modeMap(m)
      case _ => readGameMode()
    }
  }

  @tailrec
  final override def readMaxScore() = {
    print("Max score [>0]: ")
    Try(StdIn.readInt()) match {
      case Success(m) if m > 0 => m
      case _ => readMaxScore()
    }
  }

  @tailrec
  final override def readPlayerName() = {
    print("Enter your name: ")
    Try(StdIn.readLine()) match {
      case Success(str) if str.trim.nonEmpty => str.trim
      case _ => readPlayerName()
    }
  }

  @tailrec
  final override def readPlayerGesture() = {
    print(s"Pick a gesture [${gameGestures.map(_.name).mkString(",")}]: ")
    Try(StdIn.readLine()) match {
      case Success(str) =>
        gameGestures.find(_.name.toLowerCase.startsWith(str.toLowerCase)) match {
          case Some(g) => g
          case _ => readPlayerGesture()
        }
      case _ => readPlayerGesture()
    }
  }

  @tailrec
  final override def readPlayAgain() = {
    print(s"Do you wish to play again? [Y/N]: ")
    Try(StdIn.readChar()) match {
      case Success(c) if c.toUpper == 'Y' => println(); true
      case Success(c) if c.toUpper == 'N' => println(); false
      case _ => readPlayAgain()
    }
  }
}
