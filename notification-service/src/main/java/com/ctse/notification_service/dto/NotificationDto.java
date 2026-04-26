package com.ctse.notification_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private String id;
    private String recipientId;
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;
}
