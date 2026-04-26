package com.ctse.appointment_service.controller;

import com.ctse.appointment_service.dto.CreateSlotRequest;
import com.ctse.appointment_service.dto.UpdateSlotRequest;
import com.ctse.appointment_service.dto.SlotDto;
import com.ctse.appointment_service.model.AppointmentSlot;
import com.ctse.appointment_service.service.AppointmentSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
public class SlotController {

    private final AppointmentSlotService slotService;

    public SlotController(AppointmentSlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    public ResponseEntity<SlotDto> createSlot(@RequestBody CreateSlotRequest request) {
        AppointmentSlot created = slotService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam(required = false) LocalDate date) {
        List<SlotDto> slots = slotService.getAvailableSlots(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slots);
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<SlotDto> updateSlot(
            @PathVariable String slotId,
            @RequestBody UpdateSlotRequest request) {
        AppointmentSlot updated = slotService.update(slotId, request);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String slotId) {
        slotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{slotId}/book")
    public ResponseEntity<SlotDto> bookSlot(@PathVariable String slotId) {
        AppointmentSlot slot = slotService.book(slotId);
        return ResponseEntity.ok(convertToDto(slot));
    }

    @PutMapping("/{slotId}/release")
    public ResponseEntity<SlotDto> releaseSlot(@PathVariable String slotId) {
        AppointmentSlot slot = slotService.release(slotId);
        return ResponseEntity.ok(convertToDto(slot));
    }

    private SlotDto convertToDto(AppointmentSlot slot) {
        return SlotDto.builder()
                .id(slot.getId())
                .doctorName(slot.getDoctorName())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus())
                .createdAt(slot.getCreatedAt())
                .build();
    }
}
