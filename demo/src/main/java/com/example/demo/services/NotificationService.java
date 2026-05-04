package com.example.demo.services;

import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Employee;
import com.example.demo.entities.Notification;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void notifyAdmins(String message, String type, String targetUrl, String requesterId) {
        System.out.println("[NotificationService] Searching for admins with role: %ADMIN%");
        List<Employee> admins = employeeRepository.findByRoleName("%ADMIN%");
        System.out.println("[NotificationService] Found " + admins.size() + " admins.");
        
        for (Employee admin : admins) {
            // SKIP the person who made the request
            if (admin.getEmpId().equals(requesterId)) {
                System.out.println("[NotificationService] Skipping notification for requester (Admin): " + admin.getFirstName());
                continue;
            }
            
            System.out.println("[NotificationService] Sending notification to Admin: " + admin.getFirstName() + " (ID: " + admin.getEmpId() + ")");
            createNotification(message, type, admin, targetUrl);
        }
    }

    public void createNotification(String message, String type, Employee employee, String targetUrl) {
        Notification notification = new Notification(message, type, employee, targetUrl);
        notificationRepository.save(notification);
    }

    public ResponseEntity<?> getNotifications(String empId) {
        return ResponseEntity.ok(
                ApiResponse.success("Success", notificationRepository.findByEmployeeEmpIdAndTypeNotOrderByCreatedAtDesc(empId, "INFO")));
    }

    public ResponseEntity<?> getUnreadCount(String empId) {
        return ResponseEntity
                .ok(ApiResponse.success("Success", notificationRepository.countByEmployeeEmpIdAndReadFalseAndTypeNot(empId, "INFO")));
    }

    @Transactional
    public ResponseEntity<?> markAsRead(String notificationId) {
        int updated = notificationRepository.markAsReadNative(notificationId);
        if (updated > 0) {
            return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
        }
        return ResponseEntity.notFound().build();
    }
}
