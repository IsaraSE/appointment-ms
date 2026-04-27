package com.ctse.appointment_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Appointment slot document stored in MongoDB (collection: appointment_slots).
 * Maps from the schema: slot_id, doctor_name, date, start_time, end_time, status, created_at.
 */
@Document(collection = "appointment_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AppointmentSlot {

    @Id
    private String id;

    private String doctorId;
    /** Consultant / doctor name */
    private String doctorName;

    /** Appointment date */
    private LocalDate date;

    /** Slot start time (e.g. "09:00") */
    private String startTime;

    /** Slot end time (e.g. "10:00") */
    private String endTime;

    /** AVAILABLE or BOOKED */
    private String status;

    /** Created timestamp */
    private Instant createdAt;

    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_BOOKED = "BOOKED";
}
