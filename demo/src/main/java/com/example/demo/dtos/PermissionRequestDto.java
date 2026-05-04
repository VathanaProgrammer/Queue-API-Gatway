package com.example.demo.dtos;

import java.time.LocalDateTime;

public record PermissionRequestDto(
    String permissionType,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    Double totalDays,
    String reason
) {}
