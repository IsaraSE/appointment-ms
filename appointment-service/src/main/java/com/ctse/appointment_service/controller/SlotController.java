package com.ctse.appointment_service.controller;

import com.ctse.appointment_service.dto.CreateSlotRequest;
import com.ctse.appointment_service.dto.UpdateSlotRequest;
import com.ctse.appointment_service.dto.SlotDto;
import com.ctse.appointment_service.service.AppointmentSlotService;
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
public class SlotController {

    private final AppointmentSlotService slotService;

    public SlotController(AppointmentSlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    public ResponseEntity<SlotDto> createSlot(@RequestBody CreateSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(slotService.create(request));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(slotService.getAvailableSlots(date));
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<SlotDto> updateSlot(
            @PathVariable String slotId,
            @RequestBody UpdateSlotRequest request) {
        return ResponseEntity.ok(slotService.update(slotId, request));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String slotId) {
        slotService.delete(slotId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{slotId}/book")
    public ResponseEntity<SlotDto> bookSlot(@PathVariable String slotId) {
        return ResponseEntity.ok(slotService.book(slotId));
    }

    @PutMapping("/{slotId}/release")
    public ResponseEntity<SlotDto> releaseSlot(@PathVariable String slotId) {
        return ResponseEntity.ok(slotService.release(slotId));
    }

}
