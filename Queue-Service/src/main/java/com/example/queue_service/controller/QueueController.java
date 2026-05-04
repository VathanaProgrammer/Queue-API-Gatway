package com.example.queue_service.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "*") // For Flutter/Web access
public class QueueController {

    // A Sink is a thread-safe "channel" that allows us to push data to many subscribers
    private final Sinks.Many<Map<String, String>> alertSink = Sinks.many().multicast().onBackpressureBuffer();

    /**
     * NATIVE REAL-TIME ENDPOINT (SSE)
     * Your Dashboard connects here to receive live alerts.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, String>>> streamAlerts() {
        return alertSink.asFlux()
                .map(data -> ServerSentEvent.<Map<String, String>>builder()
                        .data(data)
                        .build())
                .mergeWith(Flux.interval(Duration.ofSeconds(15)) // Keep-alive heartbeat
                        .map(i -> ServerSentEvent.<Map<String, String>>builder()
                                .comment("heartbeat")
                                .build()));
    }

    /**
     * TRIGGER ENDPOINT
     * The API Gateway calls this when it detects a visitor.
     */
    @PostMapping("/trigger")
    public void triggerAlert(@RequestBody Map<String, String> payload) {
        String ip = payload.getOrDefault("ip", "Unknown");
        String path = payload.getOrDefault("path", "/");
        
        System.out.println("\n🚨 [SURVEILLANCE] New Alert Received from Gateway!");
        System.out.println("📍 IP: " + ip + " | 🔗 Path: " + path);

        // Push data to the SSE stream instantly
        alertSink.tryEmitNext(Map.of(
                "title", "Vathana, Someone is here!",
                "body", "📍 IP: " + ip + " | 📱 Time: " + LocalTime.now(),
                "path", path,
                "timestamp", LocalTime.now().toString()
        ));
    }

    @GetMapping("/trigger")
    public String getTest() {

        return "\n🚨 [SURVEILLANCE] New Alert Received from Gateway!";

    }
}
