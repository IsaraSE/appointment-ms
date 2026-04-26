package com.ctse.notification_service.controller;

import com.ctse.notification_service.dto.NotificationDto;
import com.ctse.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{recipientId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(recipientId));
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDto> updateNotification(@PathVariable String id, @RequestBody NotificationDto notificationDto) {
        return notificationService.updateNotification(id, notificationDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        if (notificationService.deleteNotification(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
