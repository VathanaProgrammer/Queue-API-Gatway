package com.example.demo.dtos;

import java.util.List;

public record JwtResponse(
        String id,
        String username,
        List<String> roles
) {}