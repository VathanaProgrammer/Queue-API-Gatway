package com.example.demo.repositories;

import com.example.demo.entities.ResignationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResignationRequestRepository extends JpaRepository<ResignationRequest, String> {
    List<ResignationRequest> findByEmployeeEmpId(String empId);
}
