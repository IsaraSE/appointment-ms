package com.ctse.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotDto {
    private String id;
    private String doctorId;
    private String doctorName;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String status;
    private Instant createdAt;
}
