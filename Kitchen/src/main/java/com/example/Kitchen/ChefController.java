package com.example.Kitchen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChefController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/cook")
    public String chef() {
        return "Chef ran on port: " + port ;
    }
}
