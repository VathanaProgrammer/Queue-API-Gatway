package com.example.demo.services;

import com.example.demo.dtos.DepartmentRequest;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Department;
import com.example.demo.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public ResponseEntity<?> saveDepartment(DepartmentRequest request) {
        if(departmentRepository.existsByDeptName(request.deptName())){
            return ResponseEntity.status(409).body(ApiResponse.error("Department already exist!"));
        }

        Department department = new Department(request.deptName());
        departmentRepository.save(department);
        return ResponseEntity.ok(ApiResponse.success("Department created successfully!", null));
    }

    public ResponseEntity<?> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success("Success", departmentRepository.findAll()));
    }

    public ResponseEntity<?> updateDepartment(String id, DepartmentRequest request) {
        return departmentRepository.findById(id).map(department -> {
            department.setDeptName(request.deptName());
            departmentRepository.save(department);
            return ResponseEntity.ok(ApiResponse.success("Department updated successfully!", null));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> deleteDepartment(String id) {
        return departmentRepository.findById(id).map(department -> {
            departmentRepository.delete(department);
            return ResponseEntity.ok(ApiResponse.success("Department deleted successfully!", null));
        }).orElse(ResponseEntity.notFound().build());
    }
}
