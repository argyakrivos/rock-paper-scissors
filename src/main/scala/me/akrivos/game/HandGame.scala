package me.akrivos.game

import me.akrivos.game.HandGame._

import scala.util.Random

abstract class HandGame {
  require(name.nonEmpty)
  require(gestures.size > 1)
  require(maxScore > 0)

  private var roundWinnerCallbacks = Vector.empty[Option[(Player, Score, Score)] => Unit]
  protected def onRoundWinnerEvent(winner: Option[(Player, Score, Score)]) = {
    roundWinnerCallbacks.foreach(f => f(winner))
    winner
  }
  def onRoundWinner(f: Option[(Player, Score, Score)] => Unit): Unit = {
    roundWinnerCallbacks = roundWinnerCallbacks :+ f
  }
  def name: String
  def gestures: Set[Gesture]
  def mode: GameMode
  def maxScore: Score
  def score: Map[Player, Int]
  def play(p1: Player, p2: Player): Outcome
}

object HandGame {
  type Score = Int

  sealed trait GameMode { def name: String }

  object GameMode {
    object PlayerVsComputer extends GameMode { override val name = "Player vs. Computer" }
    object ComputerVsComputer extends GameMode { override val name = "Computer vs. Computer" }
    val values = Set(PlayerVsComputer, ComputerVsComputer)
  }

  abstract class Gesture(val name: String) {
    require(name.nonEmpty)
    def beats(g: Gesture): Boolean
  }

  case class Outcome(winner: Player, winScore: Score, loser: Player, loseScore: Score)

  abstract sealed class Player {
    require(name.trim.nonEmpty)

    private var playCallbacks = Vector.empty[Gesture => Unit]
    protected def onPlayEvent(gesture: Gesture) = {
      playCallbacks.foreach(f => f(gesture))
      gesture
    }
    def onPlay(f: Gesture => Unit): Unit = {
      playCallbacks = playCallbacks :+ f
    }
    def name: String
    def play: Gesture
  }

  object Player {
    def apply(name: String, pickMove: => Gesture) = new DefaultPlayer(name, pickMove)
    def apply(pickMove: => Gesture) = new DefaultPlayer(s"Computer #${Random.nextInt(100) + 1}", pickMove)
  }

  class DefaultPlayer(override val name: String, pickMove: => Gesture) extends Player {
    override def play = {
      onPlayEvent(pickMove)
    }
  }
}
