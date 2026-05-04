package com.example.demo.dtos;

import jakarta.validation.constraints.NotNull;

public record DepartmentRequest (@NotNull String deptName) {
    public String getDeptName() { return deptName; }
}
