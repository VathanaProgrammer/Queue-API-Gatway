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

    private final String BOT_TOKEN = "8743357845:AAFlxUVDPjPZizW7uiR1fop280LMav6zK48";
    private final String CHAT_ID = "1694864242";

    @GetMapping(value = {"/", "/flow"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Mono<Resource> getFlow() {
        // BACKUP NOTIFICATION (Directly from Controller)
        sendSimpleAlert();
        return Mono.just(new ClassPathResource("static/flow.html"));
    }

    private void sendSimpleAlert() {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + 
                     "&text=🚀 Vathana, Someone is visiting the Dashboard! (Triggered from Controller)";
        org.springframework.web.reactive.function.client.WebClient.create()
            .get().uri(url).retrieve().bodyToMono(String.class)
            .subscribe(s -> System.out.println("✅ Controller Alert Sent"), e -> System.err.println("❌ Controller Alert Failed: " + e.getMessage()));
    }
}
