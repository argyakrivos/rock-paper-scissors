package me.akrivos.game

import me.akrivos.game.HandGame._
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class HandGameTests extends FunSpec with Matchers with MockitoSugar {

  class TestGesture(name: String) extends Gesture(name) {
    override def beats(g: Gesture) = false
  }

  object G1 extends Gesture("TestGesture1") {
    override def beats(g: Gesture) = g == G3
  }
  object G2 extends Gesture("TestGesture2") {
    override def beats(g: Gesture) = g == G1
  }
  object G3 extends Gesture("TestGesture1") {
    override def beats(g: Gesture) = g == G2
  }

  class TestGame(
    val name: String = "TestGame", val maxScore: Score = 1, val gestures: Set[Gesture] = Set(G1, G2)
  ) extends HandGame {
    override def mode = GameMode.ComputerVsComputer
    override def score = Map.empty
    final override def play(p1: Player, p2: Player) = {
      (p1.play, p2.play) match {
        case (g1, g2) if g1.beats(g2) =>
          onRoundWinnerEvent(Some(p1, 1, 0))
          Outcome(p1, 1, p2, 0)
        case (g1, g2) if g2.beats(g1) =>
          onRoundWinnerEvent(Some(p2, 1, 0))
          Outcome(p2, 1, p1, 0)
        case _ =>
          onRoundWinnerEvent(None)
          Outcome(p1, 1, p2, 0)
      }
    }
  }

  describe("A HandGame") {

    describe("Gesture") {
      it("should not have an empty name") {
        intercept[IllegalArgumentException] {
          new TestGesture("")
        }
      }
    }

    describe("Player") {

      it("should not have an empty name") {
        intercept[IllegalArgumentException] {
          Player("", G1)
        }
      }

      it("should raise onPlay event with gesture picked") {
        val p = Player("Player1", G1)
        val onPlay = mock[Gesture => Unit]
        p.onPlay(onPlay)
        val expected = p.play
        val captor = ArgumentCaptor.forClass(classOf[Gesture])
        verify(onPlay).apply(captor.capture())
        captor.getValue shouldEqual expected
      }
    }

    describe("On creation") {

      it("should not have an empty name") {
        intercept[IllegalArgumentException] {
          new TestGame(name = "")
        }
      }

      it("should not have less than two gestures") {
        intercept[IllegalArgumentException] {
          new TestGame(gestures = Set.empty)
        }
        intercept[IllegalArgumentException] {
          new TestGame(gestures = Set(G1))
        }
      }

      it("should not have a maxScore less or equal to zero") {
        intercept[IllegalArgumentException] {
          new TestGame(maxScore = 0)
        }
        intercept[IllegalArgumentException] {
          new TestGame(maxScore = -1)
        }
      }
    }

    describe("On each round") {

      it("should raise onRoundWinner event when Player1 is winning") {
        val game = new TestGame()
        val onRoundWinner = mock[Option[(Player, Score, Score)] => Unit]
        game.onRoundWinner(onRoundWinner)
        val p1 = Player(G1)
        val p2 = Player(G3)
        game.play(p1, p2)
        val captor = ArgumentCaptor.forClass(classOf[Option[(Player, Score, Score)]])
        verify(onRoundWinner).apply(captor.capture())
        captor.getValue shouldEqual Some((p1, 1, 0))
      }

      it("should raise onRoundWinner event when Player2 is winning") {
        val game = new TestGame()
        val onRoundWinner = mock[Option[(Player, Score, Score)] => Unit]
        game.onRoundWinner(onRoundWinner)
        val p1 = Player(G3)
        val p2 = Player(G1)
        game.play(p1, p2)
        val captor = ArgumentCaptor.forClass(classOf[Option[(Player, Score, Score)]])
        verify(onRoundWinner).apply(captor.capture())
        captor.getValue shouldEqual Some((p2, 1, 0))
      }

      it("should raise onRoundWinner event when it is a draw") {
        val game = new TestGame()
        val onRoundWinner = mock[Option[(Player, Score, Score)] => Unit]
        game.onRoundWinner(onRoundWinner)
        val p1 = Player(G2)
        val p2 = Player(G2)
        game.play(p1, p2)
        val captor = ArgumentCaptor.forClass(classOf[Option[(Player, Score, Score)]])
        verify(onRoundWinner).apply(captor.capture())
        captor.getValue shouldEqual None
      }
    }

    describe("In the end") {

      it("should return the right outcome if Player1 won") {
        val game = new TestGame()
        val p1 = Player(G1)
        val p2 = Player(G3)
        val outcome = game.play(p1, p2)
        outcome shouldEqual Outcome(p1, 1, p2, 0)
      }

      it("should return the right outcome if Player2 won") {
        val game = new TestGame()
        val p1 = Player(G3)
        val p2 = Player(G1)
        val outcome = game.play(p1, p2)
        outcome shouldEqual Outcome(p2, 1, p1, 0)
      }
    }
  }
}
