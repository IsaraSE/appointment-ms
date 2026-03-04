package com.ctse.booking_service.service;

import com.ctse.booking_service.dto.BookingRequest;
import com.ctse.booking_service.model.Booking;
import com.ctse.booking_service.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public Booking createBooking(BookingRequest request) {
        // basic validation to fail fast if controller didn't already validate
        if (request == null) {
            throw new IllegalArgumentException("BookingRequest must not be null");
        }
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (request.getSlotId() == null || request.getSlotId().isBlank()) {
            throw new IllegalArgumentException("slotId is required");
        }

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setSlotId(request.getSlotId());
        booking.setStatus("CONFIRMED");
        return repository.save(booking);
    }

    public List<Booking> getBookingsByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public Optional<Booking> cancelBooking(String bookingId) {
        Optional<Booking> bookingOpt = repository.findById(bookingId);
        bookingOpt.ifPresent(b -> {
            b.setStatus("CANCELLED");
            repository.save(b);
        });
        return bookingOpt;
    }

    public Optional<Booking> rescheduleBooking(String bookingId, String newSlotId) {
        Optional<Booking> bookingOpt = repository.findById(bookingId);
        bookingOpt.ifPresent(b -> {
            b.setSlotId(newSlotId);
            repository.save(b);
        });
        return bookingOpt;
    }
}