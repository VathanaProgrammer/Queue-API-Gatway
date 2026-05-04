package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}