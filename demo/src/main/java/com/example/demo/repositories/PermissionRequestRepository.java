package com.example.demo.repositories;

import com.example.demo.entities.PermissionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRequestRepository extends JpaRepository<PermissionRequest, String> {
    List<PermissionRequest> findByEmployeeEmpId(String empId);
    List<PermissionRequest> findByStatus(String status);
}
