package com.kizina.rscgame.entity;

import lombok.Data;

import java.util.UUID;


@Data
public class Player {
    private final String nickname;
    private final Long sessionId;
    private HandSign handSign;
    private UUID matchId;
}
