package org.systeminfo.systeminfoapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.systeminfo.systeminfoapi.websocket.SystemMonitorWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SystemMonitorWebSocketHandler systemMonitorWebSocketHandler;

    public WebSocketConfig(SystemMonitorWebSocketHandler systemMonitorWebSocketHandler) {
        this.systemMonitorWebSocketHandler = systemMonitorWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(systemMonitorWebSocketHandler, "/api/monitoring/ws/system")
                .setAllowedOrigins("*");
    }
}
