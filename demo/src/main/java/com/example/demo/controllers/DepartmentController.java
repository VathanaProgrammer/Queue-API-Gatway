package com.example.demo.controllers;

import com.example.demo.dtos.DepartmentRequest;
import com.example.demo.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/department/create")
    @PreAuthorize("hasAuthority('CAN_MANAGE_DEPARTMENTS') or hasRole('ADMIN')")
    public ResponseEntity<?> saveDepartment(@Valid @RequestBody DepartmentRequest request) {
        return departmentService.saveDepartment(request);
    }

    @GetMapping("/department/all")
    public ResponseEntity<?> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PutMapping("/department/update/{id}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_DEPARTMENTS') or hasRole('ADMIN')")
    public ResponseEntity<?> updateDepartment(@PathVariable String id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/department/delete/{id}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_DEPARTMENTS') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteDepartment(@PathVariable String id) {
        return departmentService.deleteDepartment(id);
    }
}
