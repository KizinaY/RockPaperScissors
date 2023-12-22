package com.kizina.rscgame.service;

import com.kizina.rscgame.entity.Player;
import com.kizina.rscgame.repository.SessionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageService {

    private final SessionRepository sessionRepository;

    public void sendMessage(Player player, String message) {
        sendMessage(player.getSessionId(), message);
    }

    public void sendMessage(Long sessionId, String message) {
        sessionRepository.get(sessionId)
                .ifPresent(session -> session.write(message));
    }
}
