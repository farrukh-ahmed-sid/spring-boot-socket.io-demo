package com.springbootwebsocketexample.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.springbootwebsocketexample.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SocketIOService {

    private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
    private final String DISCONNECT_EVENT = "disconnect_event";
    private final String PING_EVENT = "ping_event";
    private final String LOCATION_SERVICE_EVENT = "location_service_event";

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private UserService messageService;

    @PostConstruct
    private void autoStartup() {
        start();
    }

    @PreDestroy
    private void autoStop() {
        stop();
    }

    public void start() {
        // Listening Clients Connections
        socketIOServer.addConnectListener(client -> {
            log.info("************ Client: " + getIpByClient(client) + " Connected ************");
            //client.sendEvent("connected", "You're connected successfully...");

            String userId = getParamsByClient(client);

            if (userId != null) {
                clientMap.put(userId, client);
            }
        });

        // Listening Client Disconnect
        socketIOServer.addDisconnectListener(client -> disconnectClient(client, null, null));
        socketIOServer.addEventListener(DISCONNECT_EVENT, String.class, this::disconnectClient);

        // Service Events
        servicesEventListeners();

        // Start Services
        socketIOServer.start();
    }

    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            socketIOServer = null;
        }
    }

    private void disconnectClient(SocketIOClient client, String data, AckRequest ackRequest){
        String clientIp = getIpByClient(client);
        log.info(clientIp + " ************* " + "Client disconnected" + "*************");
        client.sendEvent("disconnect", "You're disconnected!");

        String userId = getParamsByClient(client);

        if(userId != null){
            clientMap.remove(userId);
            client.disconnect();
        }
    }

    private String getParamsByClient(SocketIOClient client) {
        // Get the client url parameter (where userId is the unique identity)
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> userIdList = params.get("userId");

        if (!CollectionUtils.isEmpty(userIdList)) {
            return userIdList.get(0);
        }
        return null;
    }

    private String getIpByClient(SocketIOClient client) {
        String sa = client.getRemoteAddress().toString();
        return sa.substring(1, sa.indexOf(":"));
    }

    private void servicesEventListeners(){

        socketIOServer.addEventListener(PING_EVENT, String.class, (client, data, ackSender) -> {
            String clientIp = getIpByClient(client);
            log.info(clientIp + " ************ Ping Event -> Data: " + data+"*************");

            //messageService.processMessage(new RequestMessage(data, WebSocketMessageType.PING_MESSAGE);
            client.sendEvent(PING_EVENT, "Data "+ data);
        });

        socketIOServer.addEventListener(LOCATION_SERVICE_EVENT, String.class, (client, data, ackSender) -> {
            String clientIp = getIpByClient(client);
            log.info(clientIp + " ************ Location Service Event -> Data: " + data+"*************");

            //PingDto dto = JsonUtil.toObject(data, PingDto.class);
            //messageService.processMessage(new RequestMessage(data, WebSocketMessageType.LOCATION_SERVICE_MESSAGE);
            client.sendEvent(LOCATION_SERVICE_EVENT, "Data "+ data);
        });
    }

}
