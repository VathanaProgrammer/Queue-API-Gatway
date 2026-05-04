package com.example.demo.dtos;

import java.time.LocalDate;
import java.util.Set;

/**
 * The Root Response for /auth/me
 * Usage in Angular: user.username, user.roles, user.employee...
 */
public record UserInfoResponse(
        String id,
        String username,
        boolean isEnabled,
        Set<String> roles,
        Set<String> permissions,
        EmployeeResponse employee
) {}
