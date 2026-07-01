package org.systeminfo.systeminfoapi.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.systeminfo.systeminfoapi.service.SystemService;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SystemMonitorWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final SystemService systemService;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService executor;

    public SystemMonitorWebSocketHandler(SystemService systemService) {
        this.systemService = systemService;
        this.objectMapper = new ObjectMapper();
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SystemMonitor-WebSocket-Broadcaster");
            t.setDaemon(true);
            return t;
        });
        startBroadcasting();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("WebSocket connection established - Client: {}", session.getRemoteAddress());
        
        String welcomeMessage = "{\"message\": \"Connected to System Monitor\", \"status\": \"CONNECTED\"}";
        session.sendMessage(new TextMessage(welcomeMessage));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("WebSocket message received - Client: {}, Message: {}", 
                session.getRemoteAddress(), message.getPayload());
        
        String command = message.getPayload();
        if ("PING".equals(command)) {
            session.sendMessage(new TextMessage("{\"type\": \"PONG\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("WebSocket connection closed - Client: {}, Status: {}", 
                session.getRemoteAddress(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error - Client: {}", session.getRemoteAddress(), exception);
    }

    private void startBroadcasting() {
        executor.scheduleAtFixedRate(() -> {
            if (!sessions.isEmpty()) {
                try {
                    String systemData = objectMapper.writeValueAsString(
                            systemService.getSystemOverview()
                    );
                    TextMessage textMessage = new TextMessage(systemData);
                    
                    for (WebSocketSession session : sessions) {
                        try {
                            if (session.isOpen()) {
                                session.sendMessage(textMessage);
                            }
                        } catch (IOException e) {
                            log.warn("Failed to send message to session", e);
                            sessions.remove(session);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error broadcasting system data", e);
                }
            }
        }, 1, 5, TimeUnit.SECONDS);
    }
}
