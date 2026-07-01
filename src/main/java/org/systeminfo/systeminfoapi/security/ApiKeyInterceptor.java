package org.systeminfo.systeminfoapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.systeminfo.systeminfoapi.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final SecurityProperties securityProperties;

    public ApiKeyInterceptor(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestUri = request.getRequestURI();
        
        if (!securityProperties.isEnabled()) {
            return true;
        }

        if (isPublicEndpoint(requestUri)) {
            return true;
        }

        String apiKey = request.getHeader("X-API-Key");
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Missing API Key for endpoint: {}", requestUri);
            throw new UnauthorizedException("API Key is missing. Please provide X-API-Key header.");
        }

        if (!apiKey.equals(securityProperties.getApiKey())) {
            log.warn("Invalid API Key for endpoint: {}", requestUri);
            throw new UnauthorizedException("Invalid API Key.");
        }

        log.debug("API Key validation passed for endpoint: {}", requestUri);
        return true;
    }

    private boolean isPublicEndpoint(String requestUri) {
        return Arrays.stream(securityProperties.getPublicEndpoints())
                .anyMatch(requestUri::startsWith);
    }
}
