package com.kizina.rscgame.repository;

import org.apache.mina.core.session.IoSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {
    private final Map<Long, IoSession> sessions = new ConcurrentHashMap<>();

    public void save(IoSession session) {
         sessions.putIfAbsent(session.getId(), session);
    }

    public IoSession remove(Long sessionId) {
        return sessions.remove(sessionId);
    }

    public Optional<IoSession> get(Long sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
}
