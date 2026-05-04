package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(@NotBlank String username, @NotBlank String password, @NotBlank String roleId) {
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRoleId() { return roleId; }
}
