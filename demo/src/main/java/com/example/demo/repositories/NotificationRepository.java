package com.example.demo.repositories;

import com.example.demo.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByEmployeeEmpIdAndTypeNotOrderByCreatedAtDesc(String empId, String type);
    long countByEmployeeEmpIdAndReadFalseAndTypeNot(String empId, String type);
    
    @Modifying
    @Query(value = "UPDATE NOTIFICATIONS SET IS_READ = 1 WHERE ID = ?1", nativeQuery = true)
    int markAsReadNative(String id);
}
