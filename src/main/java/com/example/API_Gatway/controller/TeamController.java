package com.example.API_Gatway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class TeamController {

    @GetMapping("/")
    public Map<String, Object> getTeam() {
        String ip = "unknown";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        List<String> members = Arrays.asList(
            "Sieng Vathana 7155 (Owner)",
            "Sum Sambo 07757",
            "Sokny enath",
            "Ren Makara",
            "HENG CHANSELA",
            "Leang Panhasaovordy",
            "Chhean Vimeanpichta"
        );

        return Map.of(
            "message", "Hi you are from " + ip ,
            "team_members", members
        );
    }
}
