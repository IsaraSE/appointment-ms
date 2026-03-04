package com.ctse.booking_service.controller;

import com.ctse.booking_service.dto.BookingRequest;
import com.ctse.booking_service.model.Booking;
import com.ctse.booking_service.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking booking = service.createBooking(request);
        // Booking.getId() is not available in your model, so return 201 without Location
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable String userId) {
        return ResponseEntity.ok(service.getBookingsByUser(userId));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String bookingId) {
        return service.cancelBooking(bookingId)
                .map(b -> ResponseEntity.noContent().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{bookingId}/reschedule")
    public ResponseEntity<Booking> rescheduleBooking(@PathVariable String bookingId,
                                                     @RequestParam String newSlotId) {
        return service.rescheduleBooking(bookingId, newSlotId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
