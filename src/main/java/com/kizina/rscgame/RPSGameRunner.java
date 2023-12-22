package com.kizina.rscgame;

import com.kizina.rscgame.config.ServerProperties;
import com.kizina.rscgame.repository.SessionRepository;
import com.kizina.rscgame.service.MatchService;
import com.kizina.rscgame.service.MessageService;
import com.kizina.rscgame.service.PlayerService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class RPSGameRunner {

    public static void main(String[] args) throws IOException {
        SessionRepository sessionRepository = new SessionRepository();
        MessageService messageService = new MessageService(sessionRepository);
        PlayerService playerService = new PlayerService(sessionRepository);
        MatchService matchService = new MatchService(messageService);
        RPSMessageHandler messageHandler = new RPSMessageHandler(matchService, sessionRepository, playerService, messageService);
        ServerProperties properties = ServerProperties.builder()
                .readBufferSize(2048)
                .writeTimeOutMillis(1000)
                .writeBufferSize(10000)
                .port(8082)
                .build();
        Server server = new Server(properties);
        server.start(messageHandler);
    }
}
