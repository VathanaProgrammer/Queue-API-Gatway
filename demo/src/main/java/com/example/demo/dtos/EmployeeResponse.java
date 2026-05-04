package com.example.demo.dtos;


import java.time.LocalDate;
import java.util.Set;

/**
 * Nested Employee Data
 * Usage in Angular: user.employee.firstName, user.employee.department.name...
 */
public record EmployeeResponse(
        String empId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String gender,
        LocalDate dateOfBirth,
        String nationalId,
        String maritalStatus,
        String position,
        String jobType,
        Double salary,
        LocalDate hireDate,
        String status,
        String profileImage,
        String address,
        String emergencyContactName,
        String emergencyContactPhone,
        String bankName,
        String bankAccountName,
        String bankAccountNumber,
        String educationLevel,
        String educationField,
        DepartmentDto department,
        UserAccountDto user
) {
    public record DepartmentDto(String deptId, String deptName) {}
    public record UserAccountDto(
            String id,
            String username,
            boolean isEnabled,
            Set<String> roles
    ) {}
}