package com.example.demo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO for Employee & User Registration using Java Record.
 * Records are immutable and provide compact syntax for data carriers.
 */
public record EmployeeSignupRequest(
        // Basic Info
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phone,
        String gender,
        LocalDate dateOfBirth,
        String nationalId,
        String maritalStatus,

        // Work Info
        String position,
        String jobType,
        Double salary,
        LocalDate hireDate,
        String status,
        String address,
        String emergencyContactName,
        String emergencyContactPhone,

        // Bank Info
        String bankName,
        String bankAccountName,
        String bankAccountNumber,

        // Education
        String educationLevel,
        String educationField,

        // Relationship ID
        @NotNull String deptId
) {
    public String getFullName() { return firstName + " " + lastName;}
    public String getDeptName() { return deptId.toString(); }
    public String getDeptId() { return deptId.toString(); }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getNationalId() { return nationalId; }
    public String getJobType() { return jobType; }
    public String getPosition() { return position; }
    public Double getSalary() { return salary; }
    public String getDeptIdLong() { return deptId; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getEducationLevel() { return educationLevel; }
    public String getEducationField() { return educationField; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getBankName() { return bankName; }
    public String getBankAccountName() { return bankAccountName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public LocalDate getHireDate() { return hireDate; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getProfileImage() { return ""; }

}