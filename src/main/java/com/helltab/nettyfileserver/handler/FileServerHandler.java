package com.helltab.nettyfileserver.handler;

import com.helltab.nettyfileserver.util.GeneralResponse;
import com.helltab.nettyfileserver.util.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        GeneralResponse generalResponse;
        //错误处理
        generalResponse = new GeneralResponse(HttpResponseStatus.BAD_REQUEST, "请检查你的请求方法及url", null);
        ResponseUtil.response(ctx, request, generalResponse);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.warn("{}", e);
        ctx.close();
    }

}