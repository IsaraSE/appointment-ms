package com.ctse.doctor_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
@Document(collection = "schedules")
public class Schedule {

    @Id
    private String slotId;
    private String doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status = SlotStatus.AVAILABLE;

}