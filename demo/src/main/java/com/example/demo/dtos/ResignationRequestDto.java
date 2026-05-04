package com.example.demo.dtos;

import java.time.LocalDate;

public record ResignationRequestDto(
    LocalDate resignationDate,
    String reason
) {}
