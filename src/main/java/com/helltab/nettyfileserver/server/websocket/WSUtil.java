package com.helltab.nettyfileserver.server.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author helltab
 * @version 1.0
 * @date 2021/3/7 20:40
 * @desc websocket 工具类
 */
public class WSUtil {
    private final static Map<String, Channel> channelMap = new HashMap<>();
    private final static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void addUser(String userId, Channel channel) {
        if (!channelMap.containsKey(userId)) {
            channelMap.put(userId, channel);
        }
        System.out.println(channelMap);
    }

    public static void removeUser(String userId) {
        channelMap.remove(userId);
    }

    public synchronized static void send(String msg) {
        channelMap.forEach((key, channel) -> {
            System.out.println(msg);
            channel.writeAndFlush(new TextWebSocketFrame(msg));
        });
    }
}
