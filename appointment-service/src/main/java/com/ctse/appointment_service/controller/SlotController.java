package com.ctse.appointment_service.controller;

import com.ctse.appointment_service.dto.CreateSlotRequest;
import com.ctse.appointment_service.dto.UpdateSlotRequest;
import com.ctse.appointment_service.dto.SlotDto;
import com.ctse.appointment_service.service.AppointmentSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Appointment slot API. Via API Gateway exposed as /appointments/slots, /appointments/slots/available, etc.
 * - POST /slots — Create slot (Protected Admin)
 * - GET /slots/available — Get available slots (Public)
 * - PUT /slots/{slotId} — Update slot (Protected Admin)
 * - DELETE /slots/{slotId} — Delete slot (Protected Admin)
 * - PUT /slots/{slotId}/book — Mark as booked (Internal - Booking Service)
 * - PUT /slots/{slotId}/release — Release after cancellation (Internal - Booking Service)
 */
@RestController
@RequestMapping("/slots")
@Tag(name = "Appointment Slots", description = "APIs for managing appointment slot lifecycle")
public class SlotController {

    private final AppointmentSlotService slotService;

    public SlotController(AppointmentSlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    @Operation(summary = "Create an appointment slot", description = "Admin creates a new available time slot for a doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Slot created successfully",
                content = @Content(schema = @Schema(implementation = SlotDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<SlotDto> createSlot(@RequestBody CreateSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(slotService.create(request));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available slots", description = "List all open slots, optionally filtered by date")
    @ApiResponse(responseCode = "200", description = "Available slots retrieved")
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @Parameter(description = "Filter by date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotService.getAvailableSlots(date));
    }

    @PutMapping("/{slotId}")
    @Operation(summary = "Update a slot", description = "Admin updates the details of an existing slot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot updated successfully",
                content = @Content(schema = @Schema(implementation = SlotDto.class))),
        @ApiResponse(responseCode = "404", description = "Slot not found", content = @Content)
    })
    public ResponseEntity<SlotDto> updateSlot(
            @Parameter(description = "Slot ID", required = true) @PathVariable String slotId,
            @RequestBody UpdateSlotRequest request) {
        return ResponseEntity.ok(slotService.update(slotId, request));
    }

    @DeleteMapping("/{slotId}")
    @Operation(summary = "Delete a slot", description = "Admin deletes a slot from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Slot deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Slot not found", content = @Content)
    })
    public ResponseEntity<Void> deleteSlot(
            @Parameter(description = "Slot ID", required = true) @PathVariable String slotId) {
        slotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{slotId}/book")
    @Operation(summary = "Book a slot", description = "Mark a slot as BOOKED and publish booking event to RabbitMQ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot booked successfully",
                content = @Content(schema = @Schema(implementation = SlotDto.class))),
        @ApiResponse(responseCode = "404", description = "Slot not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "Slot is already booked", content = @Content)
    })
    public ResponseEntity<SlotDto> bookSlot(
            @Parameter(description = "Slot ID", required = true) @PathVariable String slotId) {
        return ResponseEntity.ok(slotService.book(slotId));
    }

    @PutMapping("/{slotId}/confirm")
    @Operation(summary = "Confirm booking with user validation", description = "Validates user via Patient Service (Feign) then books slot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking confirmed",
                content = @Content(schema = @Schema(implementation = SlotDto.class))),
        @ApiResponse(responseCode = "404", description = "User or Slot not found", content = @Content)
    })
    public ResponseEntity<SlotDto> confirmBooking(
            @Parameter(description = "Slot ID", required = true) @PathVariable String slotId,
            @Parameter(description = "User ID", required = true) @RequestParam String userId) {
        return ResponseEntity.ok(slotService.confirmBooking(slotId, userId));
    }

    @PutMapping("/{slotId}/release")
    @Operation(summary = "Release a slot", description = "Release a booked slot back to AVAILABLE status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot released successfully",
                content = @Content(schema = @Schema(implementation = SlotDto.class))),
        @ApiResponse(responseCode = "404", description = "Slot not found", content = @Content)
    })
    public ResponseEntity<SlotDto> releaseSlot(
            @Parameter(description = "Slot ID", required = true) @PathVariable String slotId) {
        return ResponseEntity.ok(slotService.release(slotId));
    }

}
