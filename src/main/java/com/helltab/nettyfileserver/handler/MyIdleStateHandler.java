package com.helltab.nettyfileserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class MyIdleStateHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state().equals(IdleState.READER_IDLE)) {
            System.out.println("read idle");
        }
        String msg = "空闲超时";
        switch (event.state()) {
            case ALL_IDLE:
                msg = "读写空闲";
                System.out.println(msg);
                break;
            case READER_IDLE:
                msg = "读空闲";
                System.out.println(msg);
                break;
            case WRITER_IDLE:
                msg = "写空闲";
                System.out.println(msg);
                break;
            default:
                break;
        }
        System.out.println(msg);
        ctx.channel().writeAndFlush(
                new TextWebSocketFrame("服务器定时推送: " + msg));

    }
}
