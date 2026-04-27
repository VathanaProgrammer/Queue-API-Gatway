package com.example.API_Gatway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@Component
public class TelegramNotificationFilter implements GlobalFilter, Ordered {

    private final com.example.API_Gatway.service.FirebaseService firebaseService;

    public TelegramNotificationFilter(com.example.API_Gatway.service.FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
        System.out.println("!!! [ALARM] MULTI-CHANNEL SURVEILLANCE FILTER ACTIVE (TELEGRAM + FIREBASE) !!!");
    }

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            org.springframework.http.server.reactive.ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            
            // GLOBAL ALARM
            System.out.println(">>> [GATEWAY] HIT DETECTED! Path: " + path + " | IP: " + (request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "Unknown"));

            // CATCH EVERYTHING: /, /flow, /flow.html
            boolean isDashboard = path.equals("/") || path.equalsIgnoreCase("/flow") || path.toLowerCase().contains("flow.html");

            if (isDashboard) {
                System.out.println("!!! [ALARM] TRIGGERING MULTI-CHANNEL ALERT NOW !!!");
                String ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "Unknown";
                String userAgent = request.getHeaders().getFirst("User-Agent");
                if (userAgent == null) userAgent = "Unknown Device";

                // 1. Send Telegram
                String message = String.format(
                    "🚨 *VATHANA! VISITOR ALERT!* 🚨\n\n" +
                    "📍 *IP:* %s\n" +
                    "📱 *Device:* %s\n" +
                    "🔗 *Target:* %s", 
                    ip, userAgent, path
                );
                sendTelegram(message);

                // 2. Send Firebase Real-time Push
                firebaseService.sendRealTimeAlert("Vathana, Someone is here!", "📍 IP: " + ip + " | 📱 Device: " + userAgent);
            }
        } catch (Exception e) {
            System.err.println("⚠️ [FILTER ERROR] " + e.getMessage());
        }
        return chain.filter(exchange);
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
                .doOnSuccess(s -> System.out.println("✅ Telegram Alert Sent from Filter!"))
                .doOnError(e -> System.err.println("❌ Filter Telegram Error: " + e.getMessage()))
                .subscribe();
    }

    @Override
    public int getOrder() {
        return -1; // Run before anything else
    }
}
