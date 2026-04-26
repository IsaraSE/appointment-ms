package com.ctse.doctor_service.controller;

import com.ctse.doctor_service.model.Schedule;
import com.ctse.doctor_service.dto.ScheduleDto;
import com.ctse.doctor_service.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ScheduleDto createSchedule(@RequestBody ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        schedule.setDoctorId(scheduleDto.getDoctorId());
        schedule.setDate(scheduleDto.getDate());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setStatus(scheduleDto.getStatus());
        return convertToDto(scheduleService.createSchedule(schedule));
    }

    @GetMapping
    public List<ScheduleDto> getSchedules() {
        return scheduleService.getAllSchedules().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/available")
    public List<ScheduleDto> getAvailableSlots() {
        return scheduleService.getAvailableSlots().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{slotId}")
    public ScheduleDto updateSchedule(@PathVariable String slotId,
                                   @RequestBody ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        schedule.setDoctorId(scheduleDto.getDoctorId());
        schedule.setDate(scheduleDto.getDate());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setStatus(scheduleDto.getStatus());
        return convertToDto(scheduleService.updateSchedule(slotId, schedule));
    }

    @DeleteMapping("/{slotId}")
    public void deleteSchedule(@PathVariable String slotId) {
        scheduleService.deleteSchedule(slotId);
    }

    @PutMapping("/{slotId}/book")
    public ScheduleDto bookSlot(@PathVariable String slotId) {
        return convertToDto(scheduleService.bookSlot(slotId));
    }

    @PutMapping("/{slotId}/release")
    public ScheduleDto releaseSlot(@PathVariable String slotId) {
        return convertToDto(scheduleService.releaseSlot(slotId));
    }

    private ScheduleDto convertToDto(Schedule schedule) {
        return new ScheduleDto(
                schedule.getSlotId(),
                schedule.getDoctorId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }
}