package com.helltab.nettyfileserver.handler;

import com.helltab.nettyfileserver.server.websocket.WSUtil;
import com.helltab.nettyfileserver.util.GeneralResponse;
import com.helltab.nettyfileserver.util.ResponseUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 李文浩
 * @date 2018/9/18
 */
@Slf4j
public class HttpDownloadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public HttpDownloadHandler() {
        super(false);
    }

    private String filePath = "D:/CentOS 7 64 位.vmdk";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws InterruptedException {
        String uri = request.uri();
        if (uri.startsWith("/download") && request.method().equals(HttpMethod.GET)) {
            GeneralResponse generalResponse = null;
            File file = new File(filePath);
            try {
                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
                ctx.write(response);
                ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationComplete(ChannelProgressiveFuture future)
                            throws Exception {
                        log.info("file {} transfer complete.", file.getName());
                        raf.close();
                    }

                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future,
                                                    long progress, long total) throws Exception {
                        if (total < 0) {
                            log.warn("file {} transfer progress: {}", file.getName(), progress);
                            WSUtil.send(file.getName() + ":" + progress);
                        } else {
                            log.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
                            WSUtil.send(file.getName() + ":" + (progress * 100 / total) + "%");
                        }
                    }
                });
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } catch (FileNotFoundException e) {
                log.warn("file {} not found", file.getPath());
                generalResponse = new GeneralResponse(HttpResponseStatus.NOT_FOUND, String.format("file %s not found", file.getPath()), null);
                ResponseUtil.response(ctx, request, generalResponse);
            } catch (IOException e) {
                log.warn("file {} has a IOException: {}", file.getName(), e.getMessage());
                generalResponse = new GeneralResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, String.format("读取 file %s 发生异常", filePath), null);
                ResponseUtil.response(ctx, request, generalResponse);
            }
        } else {
            GeneralResponse generalResponse = new GeneralResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, "文件系统", null);

            ResponseUtil.response(ctx, request, generalResponse);
            ctx.fireChannelRead(request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.warn("{}", e);
        ctx.close();

    }
}
