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
    public Mono<Resource> getFlow() {
        sendTelegram("🚀 Vathana, someone just opened the Architecture Dashboard!");
        return Mono.just(new ClassPathResource("static/flow.html"));
    }

    private void sendTelegram(String message) {
        try {
            String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + encodedMsg;
            
            // Asynchronous call so it doesn't block the user
            WebClient.create().get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(); 
        } catch (Exception e) {
            System.err.println("Telegram notification failed: " + e.getMessage());
        }
    }
}
