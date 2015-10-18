package me.akrivos.game

import me.akrivos.game.HandGame._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Random

object RockPaperScissors {

  val Name = "Rock-paper-scissors"
  val Modes = HandGame.GameMode.values
  val Gestures = Set(Rock, Paper, Scissors)

  def apply(mode: GameMode, maxScore: Score): HandGame = {
    new RockPaperScissors(Name, Gestures, mode, maxScore)
  }

  object Rock extends Gesture("Rock") {
    override def beats(g: Gesture) = g == Scissors
  }

  object Paper extends Gesture("Paper") {
    override def beats(g: Gesture) = g == Rock
  }

  object Scissors extends Gesture("Scissors") {
    override def beats(g: Gesture) = g == Paper
  }

  def dummyComputerMoveLogic = {
    val rnd = new Random()
    rnd.nextInt(3) match {
      case 0 => Rock
      case 1 => Paper
      case _ => Scissors
    }
  }
}

class RockPaperScissors(
  val name: String, val gestures: Set[Gesture], val mode: GameMode, val maxScore: Score
) extends HandGame {

  private val _score = mutable.Map.empty[Player, Score].withDefaultValue(0)
  override def score = _score.toMap

  @tailrec
  final def play(p1: Player, p2: Player): Outcome = {
    (p1.play, p2.play) match {
      case (p1Gesture, p2Gesture) if p1Gesture.beats(p2Gesture) =>
        _score += (p1 -> (_score(p1) + 1))
        onRoundWinnerEvent(Some(p1, _score(p1), _score(p2)))
      case (p1Gesture, p2Gesture) if p2Gesture.beats(p1Gesture) =>
        _score += (p2 -> (_score(p2) + 1))
        onRoundWinnerEvent(Some(p2, _score(p1), _score(p2)))
      case _ =>
        onRoundWinnerEvent(None)
    }
    (_score(p1), _score(p2)) match {
      case (p1Score, p2Score) if p1Score == maxScore => Outcome(p1, p1Score, p2, p2Score)
      case (p1Score, p2Score) if p2Score == maxScore => Outcome(p2, p2Score, p1, p1Score)
      case (p1Score, p2Score) => play(p1, p2)
    }
  }
}
