package com.helltab.nettyfileserver.server.http;

import com.helltab.nettyfileserver.handler.FileServerHandler;
import com.helltab.nettyfileserver.handler.HttpDownloadHandler;
import com.helltab.nettyfileserver.handler.HttpUploadHandler;
import com.helltab.nettyfileserver.handler.MyTextWebsocketFrameHandller;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class FileServerInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 512 * 1024
     */
    private final static int MAX_CONTENTLENGTH = 1 << 19;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        EventExecutorGroup executors = new DefaultEventExecutorGroup(1);
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec()) //编码
                .addLast(executors, new HttpUploadHandler())
                .addLast(new HttpObjectAggregator(MAX_CONTENTLENGTH))//消息聚合
                .addLast(new ChunkedWriteHandler()) //块写入
                .addLast(new HttpDownloadHandler())
                .addLast(new FileServerHandler())
                .addLast(new WebSocketServerProtocolHandler("/hello"))
                .addLast(new MyTextWebsocketFrameHandller());
    }
}
