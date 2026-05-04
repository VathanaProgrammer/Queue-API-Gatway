package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(@NotBlank String accessToken, @NotBlank String refreshToken) {
}
