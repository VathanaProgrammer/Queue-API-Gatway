package com.example.API_Gatway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/authFallback")
    public Mono<String> authServiceFallback() {
        return Mono.just("Authentication Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/queueFallback")
    public Mono<String> queueServiceFallback() {
        return Mono.just("The Queue Service is currently unavailable. We are working to restore it!");
    }

    @GetMapping("/notificationFallback")
    public Mono<String> notificationServiceFallback() {
        return Mono.just("Notification service is temporarily unavailable.");
    }
}
