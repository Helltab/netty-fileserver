package com.helltab.nettyfileserver;

import com.helltab.nettyfileserver.server.http.FileServer;
import com.helltab.nettyfileserver.server.websocket.WebsocketServer;

/**
 * 启动
 */
public class BootstrapApp {
    public static void main(String[] args) {
        new Thread(() -> {
            FileServer fileServer = new FileServer();
            fileServer.run();
        }).start();
        new Thread(() -> {
            WebsocketServer websocketServer = new WebsocketServer();
            websocketServer.run();
        }).start();

    }
}
