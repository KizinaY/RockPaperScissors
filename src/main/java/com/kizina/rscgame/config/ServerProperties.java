package com.kizina.rscgame.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerProperties {
    private Integer writeTimeOutMillis;
    private Integer writeBufferSize;
    private Integer readBufferSize;
    private Integer port;
}
