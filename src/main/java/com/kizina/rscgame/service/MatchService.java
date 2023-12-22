package com.kizina.rscgame.service;

import com.kizina.rscgame.entity.HandSign;
import com.kizina.rscgame.entity.Match;
import com.kizina.rscgame.entity.Player;
import com.kizina.rscgame.entity.Status;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.kizina.rscgame.entity.HandSign.*;

@RequiredArgsConstructor
public class MatchService {

    static final String UNKNOWN_HAND_SIGN_MESSAGE = String.format("Hand Sign is not present or invalid. Available signs %s",
            List.of(PAPER, ROCK, SCISSORS));

    static final String WAITING_OPPONENT_MESSAGE = "Waiting for opponent input...";

    static final String NOBODY_WIN_MESSAGE = "Nobody win, try again...";

    static final String MATCH_NOT_READY_MESSAGE = "Match is not ready";

    private final Queue<Match> waitingMatches = new LinkedList<>();

    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();

    private final MessageService messageService;

    private void attacheToMatch(Player currentPlayer) {
        synchronized (waitingMatches) {
            Match match = waitingMatches.poll();
            if (match == null) {
                match = new Match(UUID.randomUUID(), currentPlayer, Status.WAITING);
                matches.put(match.getId(), match);
                currentPlayer.setMatchId(match.getId());
                waitingMatches.add(match);
                messageService.sendMessage(currentPlayer, WAITING_OPPONENT_MESSAGE);
            } else {
                currentPlayer.setMatchId(match.getId());
                match.setSecondPlayer(currentPlayer);
                match.setStatus(Status.READY);
                notifyGameStarted(match);
            }
        }
    }

    public Match playMatch(Player currentPlayer) {
        if (currentPlayer.getMatchId() == null) {
            attacheToMatch(currentPlayer);
            return null;
        }

        return matches.computeIfPresent(currentPlayer.getMatchId(), (uuid, currentMatch) -> {
            if (!currentMatch.isReady()) {
                messageService.sendMessage(currentPlayer, MATCH_NOT_READY_MESSAGE);
                return currentMatch;
            }

            if (currentPlayer.getHandSign() == null) {
                messageService.sendMessage(currentPlayer, UNKNOWN_HAND_SIGN_MESSAGE);
                return currentMatch;
            }

            if (!currentMatch.isHandSignsReady()) {
                messageService.sendMessage(currentPlayer, WAITING_OPPONENT_MESSAGE);
                return currentMatch;
            }

            Player winner = getWinner(currentMatch);
            if (winner == null) {
                reset(currentMatch);
                messageService.sendMessage(currentMatch.getFirstPlayer(), NOBODY_WIN_MESSAGE);
                messageService.sendMessage(currentMatch.getSecondPlayer(), NOBODY_WIN_MESSAGE);
                return currentMatch;
            }
            currentMatch.setWinner(winner);
            Player looser = Objects.requireNonNull(getLooser(currentMatch));
            messageService.sendMessage(winner, prepareWinnerMessage(winner, looser));
            messageService.sendMessage(looser, prepareLooserMessage(winner, looser));
            return currentMatch;
        });
    }

    private void reset(Match match) {
        Player firstPlayer = match.getFirstPlayer();
        Player secondPlayer = match.getSecondPlayer();
        if (firstPlayer != null) {
            firstPlayer.setHandSign(null);
        }
        if (secondPlayer != null) {
            secondPlayer.setHandSign(null);
        }
    }

    public Match removeMatch(UUID matchId) {
        return matches.remove(matchId);
    }


    private void notifyGameStarted(Match match) {
        String startGameMessage = String.format("Game started %s vs %s", match.getFirstPlayer().getNickname(), match.getSecondPlayer().getNickname());
        messageService.sendMessage(match.getFirstPlayer(), startGameMessage);
        messageService.sendMessage(match.getSecondPlayer(), startGameMessage);
    }

    private String prepareWinnerMessage(Player winner, Player looser) {
        return String.format("Congrats, you won! Your command %s, Opponent command : %s",
                winner.getHandSign(),
                looser.getHandSign());
    }

    private String prepareLooserMessage(Player winner, Player looser) {
        return String.format("Sorry, you loose! Your command %s, Opponent command : %s",
                looser.getHandSign(),
                winner.getHandSign());
    }

    private Player getWinner(Match match) {
        if (isPlayersReady(match)) {
            int result = compareCommands(match.getFirstPlayer().getHandSign(), match.getSecondPlayer().getHandSign());
            if (result == 0) {
                return null;
            } else if (result == 1) {
                return match.getFirstPlayer();
            } else if (result == -1) {
                return match.getSecondPlayer();
            }
        }
        return null;
    }

    private Player getLooser(Match match) {
        if (isPlayersReady(match)) {
            int result = compareCommands(match.getFirstPlayer().getHandSign(), match.getSecondPlayer().getHandSign());
            if (result == 0) {
                return null;
            } else if (result == 1) {
                return match.getSecondPlayer();
            } else if (result == -1) {
                return match.getFirstPlayer();
            }
        }
        return null;
    }

    private boolean isPlayersReady(Match match) {
        return match != null
                && match.getFirstPlayer() != null
                && match.getSecondPlayer() != null
                && match.getFirstPlayer().getHandSign() != null
                && match.getSecondPlayer().getHandSign() != null;
    }

    private int compareCommands(HandSign firstHandSign, HandSign secondHandSign) {
        return switch (firstHandSign) {
            case ROCK -> switch (secondHandSign) {
                case ROCK -> 0;
                case PAPER -> -1;
                case SCISSORS -> 1;
            };
            case PAPER -> switch (secondHandSign) {
                case ROCK -> 1;
                case PAPER -> 0;
                case SCISSORS -> -1;
            };
            case SCISSORS -> switch (secondHandSign) {
                case ROCK -> -1;
                case PAPER -> 1;
                case SCISSORS -> 0;
            };
        };
    }
}
