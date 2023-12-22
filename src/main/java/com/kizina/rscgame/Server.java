package com.kizina.rscgame;

import com.kizina.rscgame.config.ServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j
public class Server {
    private final ServerProperties properties;

    public void start(IoHandlerAdapter handler) throws IOException {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(handler);
        acceptor.getSessionConfig().setReadBufferSize(properties.getReadBufferSize());
        acceptor.getSessionConfig().setWriteTimeout(properties.getWriteTimeOutMillis());
        acceptor.getSessionConfig().setSendBufferSize(properties.getWriteBufferSize());
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(StandardCharsets.UTF_8)));
        acceptor.bind(new InetSocketAddress(properties.getPort()));
        log.info("Server started on port : {}", properties.getPort());
    }
}
