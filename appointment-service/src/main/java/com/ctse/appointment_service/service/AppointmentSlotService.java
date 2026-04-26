package com.ctse.appointment_service.service;

import com.ctse.appointment_service.dto.CreateSlotRequest;
import com.ctse.appointment_service.dto.UpdateSlotRequest;
import com.ctse.appointment_service.model.AppointmentSlot;
import com.ctse.appointment_service.repository.AppointmentSlotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentSlotService {

    private static final String SLOT_NOT_FOUND_MSG = "Slot not found: ";

    private final AppointmentSlotRepository repository;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    public AppointmentSlotService(AppointmentSlotRepository repository, org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Create a new appointment slot (Protected - Admin). */
    public AppointmentSlot create(CreateSlotRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateSlotRequest must not be null");
        }
        if (request.getDoctorName() == null || request.getDoctorName().isBlank()) {
            throw new IllegalArgumentException("doctorName is required");
        }
        if (request.getDate() == null) {
            throw new IllegalArgumentException("date is required");
        }
        if (request.getStartTime() == null || request.getStartTime().isBlank()) {
            throw new IllegalArgumentException("startTime is required");
        }
        if (request.getEndTime() == null || request.getEndTime().isBlank()) {
            throw new IllegalArgumentException("endTime is required");
        }

        AppointmentSlot slot = AppointmentSlot.builder()
                .doctorName(request.getDoctorName())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(AppointmentSlot.STATUS_AVAILABLE)
                .createdAt(Instant.now())
                .build();
        return repository.save(slot);
    }

    /** Get all available slots, optionally filtered by date (Public). */
    public List<AppointmentSlot> getAvailableSlots(LocalDate date) {
        if (date != null) {
            return repository.findByStatusAndDate(AppointmentSlot.STATUS_AVAILABLE, date);
        }
        return repository.findByStatus(AppointmentSlot.STATUS_AVAILABLE);
    }

    /** Update slot details (Protected - Admin). */
    public AppointmentSlot update(String slotId, UpdateSlotRequest request) {
        AppointmentSlot slot = repository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, SLOT_NOT_FOUND_MSG + slotId));
        if (request != null) {
            if (request.getDoctorName() != null && !request.getDoctorName().isBlank()) {
                slot.setDoctorName(request.getDoctorName());
            }
            if (request.getDate() != null) {
                slot.setDate(request.getDate());
            }
            if (request.getStartTime() != null && !request.getStartTime().isBlank()) {
                slot.setStartTime(request.getStartTime());
            }
            if (request.getEndTime() != null && !request.getEndTime().isBlank()) {
                slot.setEndTime(request.getEndTime());
            }
        }
        return repository.save(slot);
    }

    /** Delete a slot (Protected - Admin). */
    public boolean delete(String slotId) {
        if (!repository.existsById(slotId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, SLOT_NOT_FOUND_MSG + slotId);
        }
        repository.deleteById(slotId);
        return true;
    }

    /** Mark slot as booked (Internal - Booking Service). */
    public AppointmentSlot book(String slotId) {
        AppointmentSlot slot = repository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, SLOT_NOT_FOUND_MSG + slotId));
        if (AppointmentSlot.STATUS_BOOKED.equals(slot.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot is already booked");
        }
        slot.setStatus(AppointmentSlot.STATUS_BOOKED);
        AppointmentSlot savedSlot = repository.save(slot);
        
        // Publish event to RabbitMQ
        rabbitTemplate.convertAndSend("appointment-exchange", "appointment.booked", "Appointment Slot Booked: " + slotId);
        
        return savedSlot;
    }

    /** Release slot after cancellation (Internal - Booking Service). */
    public AppointmentSlot release(String slotId) {
        AppointmentSlot slot = repository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, SLOT_NOT_FOUND_MSG + slotId));
        slot.setStatus(AppointmentSlot.STATUS_AVAILABLE);
        return repository.save(slot);
    }

    /** Get a single slot by id (for internal use if needed). */
    public Optional<AppointmentSlot> findById(String slotId) {
        return repository.findById(slotId);
    }
}
