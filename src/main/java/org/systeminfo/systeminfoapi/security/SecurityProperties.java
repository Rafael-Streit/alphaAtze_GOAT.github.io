package org.systeminfo.systeminfoapi.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.security")
@Data
public class SecurityProperties {
    private String apiKey = "system-monitor-api-key-2024";
    private boolean enabled = true;
    private String[] publicEndpoints = {"/api/monitoring/health", "/api/monitoring/info"};
}
