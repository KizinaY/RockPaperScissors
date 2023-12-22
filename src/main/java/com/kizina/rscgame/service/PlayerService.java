package com.kizina.rscgame.service;

import com.kizina.rscgame.entity.HandSign;
import com.kizina.rscgame.entity.Player;
import com.kizina.rscgame.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.mina.core.session.IoSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.kizina.rscgame.entity.HandSign.valueOf;

@RequiredArgsConstructor
public class PlayerService {

    private final SessionRepository sessionRepository;
    private final Map<Long, Player> players = new ConcurrentHashMap<>();

    public Player getOrCreate(long sessionId, String input) {
        return players.compute(sessionId, (_s, player) -> {
            if (player == null) {
                player = new Player(input, sessionId);
            }
            player.setHandSign(extractHandSign(input));
            return player;
        });
    }

    public void remove(long sessionId) {
        Player removed = players.remove(sessionId);
        if (removed != null) {
            IoSession removedSession = sessionRepository.remove(sessionId);
            if (removedSession != null) {
                removedSession.closeOnFlush();
            }
        }
    }

    private HandSign extractHandSign(Object message) {
        try {
            return valueOf(message.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
