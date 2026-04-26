package com.ctse.notification_service.service;

import com.ctse.notification_service.dto.NotificationDto;
import com.ctse.notification_service.model.Notification;
import com.ctse.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDto> getUserNotifications(String recipientId) {
        return notificationRepository.findByRecipientIdOrderByTimestampDesc(recipientId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();
        notification.setRecipientId(notificationDto.getRecipientId());
        notification.setTitle(notificationDto.getTitle());
        notification.setMessage(notificationDto.getMessage());
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(notificationDto.isRead());
        
        Notification saved = notificationRepository.save(notification);
        return convertToDto(saved);
    }

    public Optional<NotificationDto> updateNotification(String id, NotificationDto notificationDto) {
        return notificationRepository.findById(id).map(existing -> {
            existing.setTitle(notificationDto.getTitle());
            existing.setMessage(notificationDto.getMessage());
            existing.setRead(notificationDto.isRead());
            return convertToDto(notificationRepository.save(existing));
        });
    }

    public boolean deleteNotification(String id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
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
