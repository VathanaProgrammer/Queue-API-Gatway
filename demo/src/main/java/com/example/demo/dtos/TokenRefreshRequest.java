package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(@NotBlank String refreshToken) {
    public String getRefreshToken() { return refreshToken; }
}