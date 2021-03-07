package com.helltab.nettyfileserver.server.websocket;

import com.helltab.nettyfileserver.handler.MyIdleStateHandler;
import com.helltab.nettyfileserver.handler.MyTextWebsocketFrameHandller;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WebsocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 编解码器 HttpServerCodec
        ch.pipeline().addLast("HttpServerCodec", new HttpServerCodec());
        // 块读写
        ch.pipeline().addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
        // http 多段数据聚合
        ch.pipeline().addLast("HttpObjectAggregator", new HttpObjectAggregator(8192));
        ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(10, 15, 40, TimeUnit.SECONDS));
        ch.pipeline().addLast("MyIdleStateHandler", new MyIdleStateHandler());
        // websocket 的数据是以帧的形式传递的 frame
        // 可以看到 websocketFrame 有六个子类
        // 浏览器请求时 ws://localhost:7000/hello
        // WebSocketServerProtocolHandler 核心功能是将 http 升级为 ws 协议, 保持长链接
        ch.pipeline().addLast("WebSocketClientProtocolHandler", new WebSocketServerProtocolHandler("/hello"));

        // websocket 业务处理
        ch.pipeline().addLast("MyTextWebsocketFrameHandller", new MyTextWebsocketFrameHandller());
//        ch.pipeline().addLast("HttpServerCustomHandller", new HttpServerCustomHandller());
    }
}
