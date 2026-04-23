package com.example.API_Gatway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FlowController {

    @GetMapping("/flow")
    public String getFlow() {
        return "forward:/flow.html";
    }
}
