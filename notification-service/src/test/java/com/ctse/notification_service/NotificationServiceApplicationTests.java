package com.ctse.notification_service;

import com.ctse.notification_service.dto.NotificationDto;
import com.ctse.notification_service.model.Notification;
import com.ctse.notification_service.repository.NotificationRepository;
import com.ctse.notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceApplicationTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleNotification = new Notification();
        sampleNotification.setId("notif-001");
        sampleNotification.setRecipientId("user-001");
        sampleNotification.setTitle("Appointment Confirmed");
        sampleNotification.setMessage("Your appointment slot has been booked.");
        sampleNotification.setTimestamp(LocalDateTime.now());
        sampleNotification.setRead(false);
    }

    @Test
    void testGetUserNotifications_returnsNotificationsForRecipient() {
        when(notificationRepository.findByRecipientIdOrderByTimestampDesc("user-001"))
                .thenReturn(List.of(sampleNotification));

        List<NotificationDto> result = notificationService.getUserNotifications("user-001");

        assertEquals(1, result.size());
        assertEquals("user-001", result.get(0).getRecipientId());
        assertEquals("Appointment Confirmed", result.get(0).getTitle());
    }

    @Test
    void testGetUserNotifications_noResults_returnsEmptyList() {
        when(notificationRepository.findByRecipientIdOrderByTimestampDesc("unknown"))
                .thenReturn(List.of());

        List<NotificationDto> result = notificationService.getUserNotifications("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateNotification_savesAndReturnsDto() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(sampleNotification);

        NotificationDto inputDto = new NotificationDto();
        inputDto.setRecipientId("user-001");
        inputDto.setTitle("Appointment Confirmed");
        inputDto.setMessage("Your appointment slot has been booked.");
        inputDto.setRead(false);

        NotificationDto result = notificationService.createNotification(inputDto);

        assertNotNull(result);
        assertEquals("notif-001", result.getId());
        assertEquals("user-001", result.getRecipientId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testUpdateNotification_found_updatesAndReturnsDto() {
        when(notificationRepository.findById("notif-001")).thenReturn(Optional.of(sampleNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(sampleNotification);

        NotificationDto updateDto = new NotificationDto();
        updateDto.setTitle("Updated Title");
        updateDto.setMessage("Updated message.");
        updateDto.setRead(true);

        Optional<NotificationDto> result = notificationService.updateNotification("notif-001", updateDto);

        assertTrue(result.isPresent());
        verify(notificationRepository, times(1)).save(sampleNotification);
    }

    @Test
    void testUpdateNotification_notFound_returnsEmpty() {
        when(notificationRepository.findById("bad-id")).thenReturn(Optional.empty());

        Optional<NotificationDto> result = notificationService.updateNotification("bad-id", new NotificationDto());

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteNotification_exists_returnsTrue() {
        when(notificationRepository.existsById("notif-001")).thenReturn(true);

        boolean deleted = notificationService.deleteNotification("notif-001");

        assertTrue(deleted);
        verify(notificationRepository, times(1)).deleteById("notif-001");
    }

    @Test
    void testDeleteNotification_notFound_returnsFalse() {
        when(notificationRepository.existsById("bad-id")).thenReturn(false);

        boolean deleted = notificationService.deleteNotification("bad-id");

        assertFalse(deleted);
        verify(notificationRepository, never()).deleteById(any());
    }
}
