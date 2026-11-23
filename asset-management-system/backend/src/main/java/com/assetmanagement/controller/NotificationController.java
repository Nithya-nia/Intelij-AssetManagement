package com.assetmanagement.controller;

import com.assetmanagement.model.Notification;
import com.assetmanagement.service.NotificationSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationSchedulerService notificationSchedulerService;

    public NotificationController(NotificationSchedulerService notificationSchedulerService) {
        this.notificationSchedulerService = notificationSchedulerService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationSchedulerService.getAllNotifications());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Notification>> getAlertNotifications() {
        return ResponseEntity.ok(notificationSchedulerService.getAlertNotifications());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshNotifications() {
        notificationSchedulerService.updateSimExpiryNotifications();
        return ResponseEntity.ok(Map.of("message", "SIM expiry notifications refreshed successfully"));
    }
}
