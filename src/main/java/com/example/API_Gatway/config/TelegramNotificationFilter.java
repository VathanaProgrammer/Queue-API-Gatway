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
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@Component
public class TelegramNotificationFilter implements GlobalFilter, Ordered {

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // ONLY trigger for the Dashboard routes
        if (path.equals("/") || path.equals("/flow") || path.contains("flow.html")) {
            System.out.println("!!! [GATEWAY FILTER] DETECTED VISITOR ON: " + path);
            
            String ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "Unknown";
            String userAgent = request.getHeaders().getFirst("User-Agent");
            if (userAgent == null) userAgent = "Unknown Device";

            String message = String.format(
                "🚀 *Vathana, External Visit!* \n\n" +
                "📍 *IP:* %s\n" +
                "📱 *Device:* %s\n" +
                "🔗 *Path:* %s", 
                ip, userAgent, path
            );

            sendTelegram(message);
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
