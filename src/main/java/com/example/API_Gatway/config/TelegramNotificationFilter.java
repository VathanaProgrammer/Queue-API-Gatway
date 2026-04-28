package com.example.API_Gatway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@Component
public class TelegramNotificationFilter implements GlobalFilter, Ordered {

    public TelegramNotificationFilter() {
        System.out.println("\n🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨");
        System.out.println("✅ SURVEILLANCE FILTER ACTIVE: NATIVE REAL-TIME READY!");
        System.out.println("🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨\n");
    }

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            org.springframework.http.server.reactive.ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            
            // AGGRESSIVE MATCHING: Catch anything that looks like a landing or dashboard
            boolean isDashboard = path.equals("/") || 
                                 path.isEmpty() || 
                                 path.equalsIgnoreCase("/flow") || 
                                 path.toLowerCase().contains("flow");

            if (isDashboard) {
                System.out.println("🔎 [GATEWAY] Visitor Detected: " + path);
                
                String ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "Unknown";
                String userAgent = request.getHeaders().getFirst("User-Agent");
                if (userAgent == null) userAgent = "Unknown Device";

                // 1. Send Telegram Alert
                sendTelegram(String.format("🚨 *VISITOR!* 🚨\n\nIP: %s\nPath: %s", ip, path));

                // 2. Send Native Real-Time Alert to Queue Service
                triggerQueueServiceAlert(ip, path);
            }
        } catch (Exception e) {
            System.err.println("⚠️ [FILTER ERROR] " + e.getMessage());
        }
        return chain.filter(exchange);
    }

    private void triggerQueueServiceAlert(String ip, String path) {
        String url = "http://localhost:8081/api/queue/trigger";
        java.util.Map<String, String> payload = java.util.Map.of("ip", ip, "path", path);

        WebClient.create().post()
                .uri(url)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> System.out.println("🚀 [NATIVE] Alert pushed to Queue Service!"))
                .doOnError(e -> System.err.println("❌ Queue Service Failed: " + e.getMessage()))
                .subscribe();
    }

    private void sendTelegram(String message) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("chat_id", CHAT_ID);
        formData.add("text", message);
        formData.add("parse_mode", "Markdown");

        WebClient.create().post()
                .uri(url)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
