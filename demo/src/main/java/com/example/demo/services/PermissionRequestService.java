package com.example.demo.services;

import com.example.demo.dtos.PermissionRequestDto;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Employee;
import com.example.demo.entities.PermissionRequest;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.PermissionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionRequestService {

    @Autowired
    private PermissionRequestRepository permissionRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<?> createRequest(String empId, PermissionRequestDto dto) {
        return employeeRepository.findById(empId).map(employee -> {
            PermissionRequest request = new PermissionRequest();
            request.setEmployee(employee);
            request.setPermissionType(dto.permissionType());
            request.setStartDateTime(dto.startDateTime());
            request.setEndDateTime(dto.endDateTime());
            request.setTotalDays(dto.totalDays());
            request.setReason(dto.reason());

            permissionRequestRepository.save(request);

            // Notify all ADMINS about the new request
            notificationService.notifyAdmins(
                    "New permission request from " + employee.getFirstName() + " " + employee.getLastName(),
                    "REQUEST",
                    "/permission-requests",
                    employee.getEmpId());

            return ResponseEntity.ok(ApiResponse.success("Permission request submitted successfully!", null));
        }).orElse(ResponseEntity.badRequest().body(ApiResponse.error("Employee not found")));
    }

    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(ApiResponse.success("Success", permissionRequestRepository.findAll()));
    }

    public ResponseEntity<?> getRequestsByEmployee(String empId) {
        return ResponseEntity
                .ok(ApiResponse.success("Success", permissionRequestRepository.findByEmployeeEmpId(empId)));
    }

    public ResponseEntity<?> approveRequest(String requestId, String managerId, String comment) {
        return permissionRequestRepository.findById(requestId).map(request -> {
            return employeeRepository.findById(managerId).map(manager -> {
                request.setStatus("APPROVED");
                request.setApprovedBy(manager);
                request.setManagerComment(comment);
                permissionRequestRepository.save(request);

                // Notify employee
                notificationService.createNotification(
                        "Your permission request has been APPROVED",
                        "SUCCESS",
                        request.getEmployee(),
                        null);

                return ResponseEntity.ok(ApiResponse.success("Request approved!", null));
            }).orElse(ResponseEntity.badRequest().body(ApiResponse.error("Manager not found")));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> rejectRequest(String requestId, String managerId, String comment) {
        return permissionRequestRepository.findById(requestId).map(request -> {
            return employeeRepository.findById(managerId).map(manager -> {
                request.setStatus("REJECTED");
                request.setApprovedBy(manager);
                request.setManagerComment(comment);
                permissionRequestRepository.save(request);

                // Notify employee
                notificationService.createNotification(
                        "Your permission request has been REJECTED",
                        "ERROR",
                        request.getEmployee(),
                        null);

                return ResponseEntity.ok(ApiResponse.success("Request rejected!", null));
            }).orElse(ResponseEntity.badRequest().body(ApiResponse.error("Manager not found")));
        }).orElse(ResponseEntity.notFound().build());
    }

    //Find by employee id
    public ResponseEntity<?> getByEmployeeEmpId(String empId) {
        return ResponseEntity.ok(ApiResponse.success("Success", permissionRequestRepository.findByEmployeeEmpId(empId)));
    }
}
