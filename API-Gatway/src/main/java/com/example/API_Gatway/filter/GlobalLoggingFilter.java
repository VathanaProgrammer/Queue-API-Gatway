package com.example.API_Gatway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        ServerHttpRequest request = exchange.getRequest();

        String requestId = UUID.randomUUID().toString().substring(0, 6);
        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String ip = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        log.info("[{}] → {} {} | IP={}", requestId, method, path, ip);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            long time = System.currentTimeMillis() - startTime;

            Object route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

            log.info("[{}] ← {} | Route={} | {}ms",
                    requestId,
                    exchange.getResponse().getStatusCode(),
                    route,
                    time);
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}