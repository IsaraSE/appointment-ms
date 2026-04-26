package com.ctse.doctor_service.controller;

import com.ctse.doctor_service.dto.ScheduleDto;
import com.ctse.doctor_service.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ScheduleDto createSchedule(@RequestBody ScheduleDto scheduleDto) {
        return scheduleService.createSchedule(scheduleDto);
    }

    @GetMapping
    public List<ScheduleDto> getSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/available")
    public List<ScheduleDto> getAvailableSlots() {
        return scheduleService.getAvailableSlots();
    }

    @PutMapping("/{slotId}")
    public ScheduleDto updateSchedule(@PathVariable String slotId,
                                   @RequestBody ScheduleDto scheduleDto) {
        return scheduleService.updateSchedule(slotId, scheduleDto);
    }

    @DeleteMapping("/{slotId}")
    public void deleteSchedule(@PathVariable String slotId) {
        scheduleService.deleteSchedule(slotId);
    }

    @PutMapping("/{slotId}/book")
    public ScheduleDto bookSlot(@PathVariable String slotId) {
        return scheduleService.bookSlot(slotId);
    }

    @PutMapping("/{slotId}/release")
    public ScheduleDto releaseSlot(@PathVariable String slotId) {
        return scheduleService.releaseSlot(slotId);
    }
}