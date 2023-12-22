package com.kizina.rscgame.service;

import com.kizina.rscgame.entity.HandSign;
import com.kizina.rscgame.entity.Match;
import com.kizina.rscgame.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MatchService matchService;

    @Test
    public void shouldWaitForOpponentWhenFirstPlayerIsAttachedTest() {
        Player player = new Player("test1", 1L);
        matchService.playMatch(player);
        Mockito.verify(messageService).sendMessage(player, MatchService.WAITING_OPPONENT_MESSAGE);
    }

    @Test
    public void shouldCorrectlyProcessWinByOnePlayerTest() {
        Player player1 = new Player("test1", 1L);
        Player player2 = new Player("test2", 2L);
        matchService.playMatch(player1);

        Mockito.verify(messageService).sendMessage(player1, MatchService.WAITING_OPPONENT_MESSAGE);

        matchService.playMatch(player2);

        Mockito.verify(messageService).sendMessage(player1, "Game started test1 vs test2");
        Mockito.verify(messageService).sendMessage(player2, "Game started test1 vs test2");

        player1.setHandSign(HandSign.ROCK);
        Match match = matchService.playMatch(player1);
        Assertions.assertNotNull(match);

        player2.setHandSign(HandSign.PAPER);
        match = matchService.playMatch(player2);

        Assertions.assertNotNull(match);
        Assertions.assertTrue(match.isFinished());
        Assertions.assertSame(match.getWinner(), player2);
        Assertions.assertTrue(match.isFinished());

        Mockito.verify(messageService).sendMessage(player1, "Sorry, you loose! Your command ROCK, Opponent command : PAPER");
        Mockito.verify(messageService).sendMessage(player2, "Congrats, you won! Your command PAPER, Opponent command : ROCK");
    }

    @Test
    public void shouldCorrectlyProcessNobodyWinTest() {
        Player player1 = new Player("test1", 1L);
        Player player2 = new Player("test2", 2L);
        matchService.playMatch(player1);

        Mockito.verify(messageService).sendMessage(player1, MatchService.WAITING_OPPONENT_MESSAGE);

        matchService.playMatch(player2);

        Mockito.verify(messageService).sendMessage(player1, "Game started test1 vs test2");
        Mockito.verify(messageService).sendMessage(player2, "Game started test1 vs test2");

        player1.setHandSign(HandSign.ROCK);
        Match match = matchService.playMatch(player1);
        Assertions.assertNotNull(match);

        player2.setHandSign(HandSign.ROCK);
        match = matchService.playMatch(player2);

        Assertions.assertNotNull(match);
        Assertions.assertFalse(match.isFinished());
        Assertions.assertTrue(match.isReady());
        Assertions.assertNull(match.getWinner());

        Mockito.verify(messageService).sendMessage(player1, MatchService.NOBODY_WIN_MESSAGE);
        Mockito.verify(messageService).sendMessage(player2, MatchService.NOBODY_WIN_MESSAGE);
    }
}