package com.helltab.nettyfileserver.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author helltab
 * @version 1.0
 * @date 2021/3/7 9:04
 * @desc 文件服务器
 */
@Slf4j
public class FileServer {
    private final static int HTTP_PORT = 8882;

    private int httpPort;

    public FileServer(int httpPort) {
        this.httpPort = httpPort;
    }

    public FileServer() {
        this(HTTP_PORT);
    }

    /**
     * 创建 netty http server
     */
    public void run() {

        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.group(boosGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new FileServerInitializer());
        try {
            ChannelFuture future = serverBootstrap.bind(this.httpPort).sync();
            log.info("http://{}:{}/ start", InetAddress.getLocalHost().getHostAddress(), this.httpPort);
            future.channel().closeFuture().sync();
        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
