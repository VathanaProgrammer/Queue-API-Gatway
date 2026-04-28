package com.example.API_Gatway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@Controller
public class FlowController {

    public FlowController() {
    }

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @GetMapping(value = {"/flow"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Mono<Resource> getFlow() {
        // MULTI-CHANNEL ALERT (Controller Level)
        sendMultiChannelAlert();
        return Mono.just(new ClassPathResource("static/flow.html"));
    }

    private void sendMultiChannelAlert() {
        // 1. Telegram
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + 
                     "&text=🚀 Vathana, Someone is visiting the Dashboard! (Triggered from Controller)";
        org.springframework.web.reactive.function.client.WebClient.create()
            .get().uri(url).retrieve().bodyToMono(String.class)
            .subscribe(s -> System.out.println("✅ Controller Telegram Sent"), e -> System.err.println("❌ Controller Telegram Failed: " + e.getMessage()));

        // 2. Native Real-Time (Queue Service)
        String queueUrl = "http://localhost:8081/api/queue/trigger";
        java.util.Map<String, String> payload = java.util.Map.of("ip", "Gateway-Controller", "path", "/flow");
        org.springframework.web.reactive.function.client.WebClient.create()
            .post().uri(queueUrl).bodyValue(payload).retrieve().bodyToMono(Void.class)
            .subscribe(v -> System.out.println("🚀 [NATIVE REAL-TIME] Alert Sent from Controller"), e -> System.err.println("❌ Controller Queue Service Failed"));
    }
}
