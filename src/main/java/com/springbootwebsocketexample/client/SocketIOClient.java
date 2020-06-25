package com.springbootwebsocketexample.client;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class SocketIOClient {

    public static void main(String[] args) {
        // Server socket.io Connection Communication Address
        String url = "http://127.0.0.1:8888";
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.reconnectionAttempts = 2;
            // Time interval for failed reconnection
            options.reconnectionDelay = 1000;
            // Connection timeout (ms)
            options.timeout = 500;
            // userId: Unique identity passed to the server-side store
            final Socket socket = IO.socket(url + "?userId=1", options);

            socket.on(Socket.EVENT_CONNECT, args1 -> socket.send("hello..."));

            // Custom Event`Connected` ->Receive Server Successful Connection Message
            socket.on("connected", objects -> log.debug("Server:" + objects[0].toString()));

            // Custom Event`push_data_event` ->Receive Service-side Messages
            socket.on("push_data_event", objects -> log.debug("Server:" + objects[0].toString()));

            // Custom Event`myBroadcast` ->Receive Service-side Broadcast Messages
            socket.on("myBroadcast", objects -> log.debug("Server:" + objects[0].toString()));

            socket.connect();

            while (true) {
                Thread.sleep(3000);
                // Custom Event`push_data_event` ->Send a message to the server
                socket.emit("push_data_event", "send data " + LocalDateTime.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
