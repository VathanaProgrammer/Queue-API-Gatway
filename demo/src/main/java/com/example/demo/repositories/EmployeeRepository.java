package com.example.demo.repositories;

import com.example.demo.entities.Employee;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Employee findByEmail(String email);

    boolean existsByEmail(String email);

    Employee findByUser(User user);

    Employee findByEmpId(String empId);

    List<Employee> findByUser_EnableTrue();
    
    @Query(value = "SELECT e.* FROM employees e " +
                   "JOIN users u ON e.user_id = u.user_id " +
                   "JOIN user_roles ur ON u.user_id = ur.user_id " +
                   "JOIN roles r ON ur.role_id = r.role_id " +
                   "WHERE UPPER(r.role_name) LIKE UPPER(:roleName)", nativeQuery = true)
    List<Employee> findByRoleName(@Param("roleName") String roleName);

    Optional<Employee> findByUserUserId(String userId);
}
