package com.example.API_Gatway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class FlowController {

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @GetMapping(value = {"/", "/flow"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Mono<Resource> getFlow(org.springframework.http.server.reactive.ServerHttpRequest request) {
        String ip = request.getRemoteAddress().getHostString();
        String userAgent = request.getHeaders().getFirst("User-Agent");
        
        String message = String.format(
            "🚀 *Vathana, Someone is here!* \n\n" +
            "📍 *IP:* %s\n" +
            "📱 *Device:* %s", 
            ip, userAgent
        );

        sendTelegram(message);
        return Mono.just(new ClassPathResource("static/flow.html"));
    }

    private void sendTelegram(String message) {
        try {
            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("chat_id", CHAT_ID);
            body.put("text", message);
            body.put("parse_mode", "Markdown");

            WebClient.create().post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(s -> System.out.println("✅ Telegram Alert Sent!"))
                    .doOnError(e -> System.err.println("❌ Telegram Error: " + e.getMessage()))
                    .subscribe(); 
        } catch (Exception e) {
            System.err.println("❌ Telegram notification failed: " + e.getMessage());
        }
    }
}
