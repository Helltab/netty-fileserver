package com.helltab.nettyfileserver.handler;

import com.helltab.nettyfileserver.server.websocket.WSUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * TextWebSocketFrame 表示文本帧
 */
public class MyTextWebsocketFrameHandller extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息: " + msg.text());
        ctx.channel().writeAndFlush(
                new TextWebSocketFrame("服务器时间: "
                                               + LocalTime.now() + " [" + msg.text() + "]"));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().schedule(() -> {
            ctx.channel().writeAndFlush(
                    new TextWebSocketFrame("服务器定时发送: " + LocalTime.now()));
        }, 10, TimeUnit.SECONDS);
    }

    /**
     * web 连接后就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        WSUtil.addUser(ctx.channel().id().asLongText(), ctx.channel());
        System.out.println("handlerAdded: channel id = " + ctx.channel().id().asLongText());
        // short 不是唯一的
        System.out.println("handlerAdded: channel id(short) = " + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved: channel id = " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
