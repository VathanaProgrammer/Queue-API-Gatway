package com.example.demo.services;

import com.example.demo.dtos.ResignationRequestDto;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.ResignationRequest;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.ResignationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResignationRequestService {

    @Autowired
    private ResignationRequestRepository resignationRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<?> createRequest(String empId, ResignationRequestDto dto) {
        return employeeRepository.findById(empId).map(employee -> {
            ResignationRequest request = new ResignationRequest();
            request.setEmployee(employee);
            request.setNoticeDate(dto.resignationDate());
            request.setLastWorkingDate(dto.resignationDate()); // Set last working date to the requested date
            request.setReason(dto.reason());

            resignationRequestRepository.save(request);

            // Notify all ADMINS about the resignation
            notificationService.notifyAdmins(
                "New resignation request from " + employee.getFirstName() + " " + employee.getLastName(),
                "REQUEST",
                "/resignations",
                employee.getEmpId()
            );

            return ResponseEntity.ok(ApiResponse.success("Resignation request submitted successfully!", null));
        }).orElse(ResponseEntity.badRequest().body(ApiResponse.error("Employee not found")));
    }

    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(ApiResponse.success("Success", resignationRequestRepository.findAll()));
    }

    public ResponseEntity<?> approveRequest(String requestId, String managerId, String comment) {
        return resignationRequestRepository.findById(requestId).map(request -> {
            return employeeRepository.findById(managerId).map(manager -> {
                request.setStatus("APPROVED");
                request.setApprovedBy(manager);
                request.setFeedback(comment);
                resignationRequestRepository.save(request);

                // Notify employee
                notificationService.createNotification(
                    "Your resignation request has been APPROVED",
                    "SUCCESS",
                    request.getEmployee(),
                    null
                );
                return ResponseEntity.ok(ApiResponse.success("Resignation approved!", null));
            }).orElse(ResponseEntity.badRequest().body(ApiResponse.error("Manager not found")));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> rejectRequest(String requestId, String managerId, String comment) {
        return resignationRequestRepository.findById(requestId).map(request -> {
            return employeeRepository.findById(managerId).map(manager -> {
                request.setStatus("REJECTED");
                request.setApprovedBy(manager);
                request.setFeedback(comment);
                resignationRequestRepository.save(request);

                // Notify employee
                notificationService.createNotification(
                    "Your resignation request has been REJECTED",
                    "ERROR",
                    request.getEmployee(),
                    null
                );
                return ResponseEntity.ok(ApiResponse.success("Resignation rejected!", null));
            }).orElse(ResponseEntity.badRequest().body(ApiResponse.error("Manager not found")));
        }).orElse(ResponseEntity.notFound().build());
    }

    //Find by employee id
    public ResponseEntity<?> getByEmployeeEmpId(String empId) {
        return ResponseEntity.ok(ApiResponse.success("Success", resignationRequestRepository.findByEmployeeEmpId(empId)));
    }
}
