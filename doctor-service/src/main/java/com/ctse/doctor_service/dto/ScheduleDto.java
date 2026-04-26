package com.ctse.doctor_service.dto;

import com.ctse.doctor_service.model.SlotStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleDto {
    private String slotId;
    private String doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;

    public ScheduleDto() {}

    public ScheduleDto(String slotId, String doctorId, LocalDate date, LocalTime startTime, LocalTime endTime, SlotStatus status) {
        this.slotId = slotId;
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }
}
