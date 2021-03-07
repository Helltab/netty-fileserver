package com.helltab.nettyfileserver.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author helltab
 * @version 1.0
 * @date 2021/3/7 9:04
 * @desc 文件服务器
 */
@Slf4j
public class WebsocketServer {
    private final static int WS_PORT = 8884;

    private int wsPort;

    public WebsocketServer(int wsPort) {
        this.wsPort = wsPort;
    }

    public WebsocketServer() {
        this(WS_PORT);
    }

    /**
     * 创建 netty websocket server
     */
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new WebsocketServerInitializer());
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(wsPort).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
