package com.example.demo.controllers;

import com.example.demo.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{empId}")
    public ResponseEntity<?> getNotifications(@PathVariable String empId) {
        return notificationService.getNotifications(empId);
    }

    @GetMapping("/unread-count/{empId}")
    public ResponseEntity<?> getUnreadCount(@PathVariable String empId) {
        return notificationService.getUnreadCount(empId);
    }

    @PostMapping("/mark-as-read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        return notificationService.markAsRead(id);
    }
}
