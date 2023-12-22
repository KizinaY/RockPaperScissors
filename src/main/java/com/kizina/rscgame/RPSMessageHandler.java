package com.kizina.rscgame;

import com.kizina.rscgame.entity.Match;
import com.kizina.rscgame.entity.Player;
import com.kizina.rscgame.repository.SessionRepository;
import com.kizina.rscgame.service.MatchService;
import com.kizina.rscgame.service.MessageService;
import com.kizina.rscgame.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class RPSMessageHandler extends IoHandlerAdapter {
    private final MatchService matchService;
    private final SessionRepository sessionRepository;
    private final PlayerService playerService;
    private final MessageService messageService;

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        sessionRepository.save(session);
    }

    @Override
    public void sessionOpened(IoSession session) {
        log.debug("Session opened. Session id : {}", session.getId());
        messageService.sendMessage(session.getId(), "Please, enter nickname :");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        try {
            Player currentPlayer = playerService.getOrCreate(session.getId(), message.toString());
            Match match = matchService.playMatch(currentPlayer);
            if (match != null && match.isFinished()) {
                closeMatch(match.getId());
            }
        } catch (Exception ex) {
            log.error("Unexpected error", ex);
            messageService.sendMessage(session.getId(), "Unexpected error");
            sessionRepository.remove(session.getId());
            playerService.remove(session.getId());
        }
    }


    @Override
    public void sessionClosed(IoSession session) throws Exception {
        playerService.remove(session.getId());
        log.debug("Session closed. Session id : {}", session.getId());
    }

    private void closeMatch(UUID matchId) {
        Match removed = matchService.removeMatch(matchId);
        if (removed != null) {
            playerService.remove(removed.getFirstPlayer().getSessionId());
            playerService.remove(removed.getSecondPlayer().getSessionId());
        }
    }
}
