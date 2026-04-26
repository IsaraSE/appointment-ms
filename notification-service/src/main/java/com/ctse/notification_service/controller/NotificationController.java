package com.ctse.notification_service.controller;

import com.ctse.notification_service.model.Notification;
import com.ctse.notification_service.dto.NotificationDto;
import com.ctse.notification_service.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/user/{recipientId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable String recipientId) {
        List<NotificationDto> dtos = notificationRepository.findByRecipientIdOrderByTimestampDesc(recipientId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        Notification notification = new Notification();
        notification.setRecipientId(notificationDto.getRecipientId());
        notification.setTitle(notificationDto.getTitle());
        notification.setMessage(notificationDto.getMessage());
        notification.setTimestamp(java.time.LocalDateTime.now());
        notification.setRead(notificationDto.isRead());
        
        Notification saved = notificationRepository.save(notification);
        return ResponseEntity.ok(convertToDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDto> updateNotification(@PathVariable String id, @RequestBody NotificationDto notificationDto) {
        return notificationRepository.findById(id).map(existing -> {
            existing.setTitle(notificationDto.getTitle());
            existing.setMessage(notificationDto.getMessage());
            existing.setRead(notificationDto.isRead());
            return ResponseEntity.ok(convertToDto(notificationRepository.save(existing)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setRecipientId(notification.getRecipientId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setRead(notification.isRead());
        return dto;
    }
}
