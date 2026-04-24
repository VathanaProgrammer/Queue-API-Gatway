package com.example.API_Gatway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

@Controller
public class FlowController {

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @GetMapping(value = {"/", "/flow"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Mono<Resource> getFlow(org.springframework.http.server.reactive.ServerHttpRequest request) {
        String ip = "Unknown";
        if (request.getRemoteAddress() != null) {
            ip = request.getRemoteAddress().getHostString();
        }

        String userAgent = request.getHeaders().getFirst("User-Agent");
        if (userAgent == null) userAgent = "Unknown Device";
        
        String message = String.format(
            "🚀 *Hello Vathana, Someone is here!* \n\n" +
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
            
            org.springframework.util.MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
            formData.add("chat_id", CHAT_ID);
            formData.add("text", message);
            formData.add("parse_mode", "Markdown");

            WebClient.create().post()
                    .uri(url)
                    .body(org.springframework.web.reactive.function.BodyInserters.fromFormData(formData))
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
