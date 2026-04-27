package com.ctse.appointment_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateSlotRequest {
    private String doctorId;
    private String doctorName;
    private LocalDate date;
    private String startTime;
    private String endTime;
}
