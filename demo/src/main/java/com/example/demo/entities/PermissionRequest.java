package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PERMISSION_REQUESTS")
public class PermissionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    // ប្រភេទសំណើ៖ ANNUAL_LEAVE, SICK_LEAVE, SHORT_EXIT (ចេញក្រៅមួយភ្លែត), OTHER
    @Column(nullable = false)
    private String permissionType;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    // គិតជាចំនួនថ្ងៃ (ឧទាហរណ៍៖ 0.5 ឬ 2.0)
    private Double totalDays;

    @Column(length = 1000)
    private String reason;

    // ស្ថានភាព៖ PENDING, APPROVED, REJECTED, CANCELLED
    private String status;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approvedBy; // Manager ដែលជាអ្នកអនុម័ត

    private String managerComment;

    public PermissionRequest() {
        this.submittedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public Double getTotalDays() { return totalDays; }
    public void setTotalDays(Double totalDays) { this.totalDays = totalDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }

    public String getManagerComment() { return managerComment; }
    public void setManagerComment(String managerComment) { this.managerComment = managerComment; }
}