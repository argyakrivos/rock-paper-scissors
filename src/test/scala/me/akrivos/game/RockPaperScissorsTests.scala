package me.akrivos.game

import me.akrivos.game.HandGame._
import me.akrivos.game.RockPaperScissors._
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class RockPaperScissorsTests extends FunSpec with Matchers with MockitoSugar {

  describe("A RockPaperScissors") {

    describe("Game") {
      it("should have Rock, Paper and Scissors as gestures") {
        val game = RockPaperScissors(GameMode.ComputerVsComputer, 1)
        game.gestures shouldEqual Set(Rock, Paper, Scissors)
      }

      it("should have PlayerVsComputer and ComputerVsComputer as modes") {
        RockPaperScissors.Modes shouldEqual Set(GameMode.PlayerVsComputer, GameMode.ComputerVsComputer)
      }

      it("should continue until maxScore is reached") {
        val game = RockPaperScissors(GameMode.ComputerVsComputer, 5)
        val p1 = Player(RockPaperScissors.dummyComputerMoveLogic)
        val p2 = Player(RockPaperScissors.dummyComputerMoveLogic)
        game.score.get(p1) shouldEqual None
        game.score.get(p2) shouldEqual None
        val Outcome(winner, score, _, _) = game.play(p1, p2)
        score shouldEqual game.maxScore
        game.score.get(winner) shouldEqual Some(score)
      }

      it("should make sure the score is updating correctly") {
        val game = RockPaperScissors(GameMode.ComputerVsComputer, 2)
        val p1 = mock[Player]
        val p2 = mock[Player]
        when(p1.play).thenReturn(Rock).thenReturn(Scissors).thenReturn(Paper)
        when(p2.play).thenReturn(Scissors).thenReturn(Rock).thenReturn(Rock)
        val Outcome(winner, winScore, loser, loseScore) = game.play(p1, p2)
        game.score.get(winner) shouldEqual Some(winScore)
        game.score.get(loser) shouldEqual Some(loseScore)
      }

      it("should raise onRounderWinner on each round") {
        val game = RockPaperScissors(GameMode.ComputerVsComputer, 2)
        val onRoundWinner = mock[Option[(Player, Score, Score)] => Unit]
        game.onRoundWinner(onRoundWinner)
        val p1 = mock[Player]
        val p2 = mock[Player]
        when(p1.play).
          thenReturn(Scissors).
          thenReturn(Scissors).
          thenReturn(Paper).
          thenReturn(Rock)
        when(p2.play).
          thenReturn(Rock).
          thenReturn(Scissors).
          thenReturn(Rock).
          thenReturn(Paper)
        game.play(p1, p2)
        val captor = ArgumentCaptor.forClass(classOf[Option[(Player, Score, Score)]])
        verify(onRoundWinner, times(4)).apply(captor.capture())
        captor.getAllValues.get(0) shouldEqual Some(p2, 0, 1)
        captor.getAllValues.get(1) shouldEqual None
        captor.getAllValues.get(2) shouldEqual Some(p1, 1, 1)
        captor.getAllValues.get(3) shouldEqual Some(p2, 1, 2)
      }
    }

    describe("Gesture Rock") {
      it("should beat Scissors") {
        Rock.beats(Scissors) shouldEqual true
      }

      it("should not beat Paper") {
        Rock.beats(Paper) shouldEqual false
      }

      it("should not beat itself") {
        Rock.beats(Rock) shouldEqual false
      }
    }

    describe("Gesture Paper") {
      it("should beat Rock") {
        Paper.beats(Rock) shouldEqual true
      }

      it("should not beat Scissors") {
        Paper.beats(Scissors) shouldEqual false
      }

      it("should not beat itself") {
        Paper.beats(Paper) shouldEqual false
      }
    }

    describe("Gesture Scissors") {
      it("should beat Paper") {
        Scissors.beats(Paper) shouldEqual true
      }

      it("should not beat Rock") {
        Scissors.beats(Rock) shouldEqual false
      }

      it("should not beat itself") {
        Scissors.beats(Scissors) shouldEqual false
      }
    }
  }
}
