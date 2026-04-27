package com.ctse.notification_service.controller;

import com.ctse.notification_service.dto.NotificationDto;
import com.ctse.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "APIs for retrieving and managing user notifications (event-driven via RabbitMQ)")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{recipientId}")
    @Operation(summary = "Get notifications for a user",
            description = "Returns all notifications for the given recipient, ordered by most recent first")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No notifications found for this user", content = @Content)
    })
    public ResponseEntity<List<NotificationDto>> getUserNotifications(
            @Parameter(description = "Recipient user ID", required = true) @PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(recipientId));
    }

    @PostMapping
    @Operation(summary = "Create a notification",
            description = "Manually create a notification record (primarily used internally; notifications are auto-created via RabbitMQ events)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification created successfully",
                content = @Content(schema = @Schema(implementation = NotificationDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a notification", description = "Update notification details such as marking it as read")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification updated successfully",
                content = @Content(schema = @Schema(implementation = NotificationDto.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    public ResponseEntity<NotificationDto> updateNotification(
            @Parameter(description = "Notification ID", required = true) @PathVariable String id,
            @RequestBody NotificationDto notificationDto) {
        return notificationService.updateNotification(id, notificationDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification", description = "Remove a notification record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "Notification ID", required = true) @PathVariable String id) {
        if (notificationService.deleteNotification(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
