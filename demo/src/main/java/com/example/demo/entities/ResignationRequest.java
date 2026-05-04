package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESIGNATION_REQUESTS")
public class ResignationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate noticeDate; // ថ្ងៃដែលគាត់ដាក់ពាក្យសុំឈប់

    @Column(nullable = false)
    private LocalDate lastWorkingDate; // ថ្ងៃចុងក្រោយដែលគាត់ត្រូវធ្វើការ (Last day)

    @Column(length = 1000)
    private String reason; // មូលហេតុនៃការឈប់

    private String status; // PENDING, APPROVED, REJECTED, COMPLETED

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    // សម្រាប់សម្ភាសន៍ពេលឈប់ (Exit Interview)
    private String feedback;

    public ResignationRequest() {
        this.submittedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LocalDate getNoticeDate() { return noticeDate; }
    public void setNoticeDate(LocalDate noticeDate) { this.noticeDate = noticeDate; }
    public LocalDate getLastWorkingDate() { return lastWorkingDate; }
    public void setLastWorkingDate(LocalDate lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }


    // Getters and Setters...
}