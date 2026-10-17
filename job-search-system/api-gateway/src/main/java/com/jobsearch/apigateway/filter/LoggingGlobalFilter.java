package com.jobsearch.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        String requestId = request.getId();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path = request.getURI().getRawPath();
        String queryParams = request.getURI().getRawQuery();
        String fullPath = queryParams != null ? path + "?" + queryParams : path;
        String remoteAddress = request.getRemoteAddress() != null 
                ? request.getRemoteAddress().toString() 
                : "unknown";

        log.info("[REQUEST] ID: {} | Method: {} | Path: {} | Remote: {}",
                requestId, method, fullPath, remoteAddress);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = response.getStatusCode() != null 
                    ? response.getStatusCode().value() 
                    : 0;

            log.info("[RESPONSE] ID: {} | Method: {} | Path: {} | Status: {} | Duration: {}ms",
                    requestId, method, fullPath, statusCode, duration);
        }));
    }

    @Override
    public int getOrder() {
        // High precedence so it wraps all downstream filters and records total time
        return Ordered.LOWEST_PRECEDENCE;
    }
}
