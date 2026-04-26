package com.ctse.notification_service.listener;

import com.ctse.notification_service.model.Notification;
import com.ctse.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationRepository notificationRepository;

    public NotificationListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = "appointment-notification-queue")
    public void receiveMessage(String message) {
        log.info("Received Notification Event: {}", message);
        
        Notification notification = new Notification();
        notification.setRecipientId("SYSTEM"); // Default system recipient for raw string events
        notification.setTitle("New Appointment Event");
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        
        notificationRepository.save(notification);
        log.info("Notification successfully saved to MongoDB");
    }
}
