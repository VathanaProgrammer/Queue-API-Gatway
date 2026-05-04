package com.example.demo.controllers;

import com.example.demo.dtos.PermissionRequestDto;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Employee;
import com.example.demo.entities.User;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.services.PermissionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RestController
@RequestMapping("/api/permission")
public class PermissionRequestController {

    @Autowired
    private PermissionRequestService permissionRequestService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/request/{empId}")
    public ResponseEntity<?> createRequest(@PathVariable String empId, @RequestBody PermissionRequestDto dto) {
        return permissionRequestService.createRequest(empId, dto);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CAN_MANAGE_LEAVE') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllRequests() {
        return permissionRequestService.getAllRequests();
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('CAN_MANAGE_LEAVE') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllPermissionByEmpId(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Optional<Employee> employee = employeeRepository.findByUserUserId(userDetails.getId());
        if(employee.isEmpty()){
            return ResponseEntity.status(404).body(ApiResponse.error("Employee not found!"));
        }

        String empId = employee.get().getEmpId();

        if(empId == null){
            return ResponseEntity.status(404).body(ApiResponse.error("Employee not found!"));
        }

        return ResponseEntity.ok(permissionRequestService.getByEmployeeEmpId(empId));
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getRequestsByEmployee(@PathVariable String empId) {
        return permissionRequestService.getRequestsByEmployee(empId);
    }

    @PostMapping("/approve/{requestId}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_LEAVE') or hasRole('ADMIN')")
    public ResponseEntity<?> approveRequest(
            @PathVariable String requestId, 
            @RequestParam String managerId, 
            @RequestParam(required = false) String comment) {
        return permissionRequestService.approveRequest(requestId, managerId, comment);
    }

    @PostMapping("/reject/{requestId}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_LEAVE') or hasRole('ADMIN')")
    public ResponseEntity<?> rejectRequest(
            @PathVariable String requestId, 
            @RequestParam String managerId, 
            @RequestParam(required = false) String comment) {
        return permissionRequestService.rejectRequest(requestId, managerId, comment);
    }
}
