package com.ctse.doctor_service.service;

import com.ctse.doctor_service.model.Schedule;
import com.ctse.doctor_service.model.SlotStatus;
import com.ctse.doctor_service.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Schedule createSchedule(Schedule schedule) {
        schedule.setStatus(SlotStatus.AVAILABLE);
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getAvailableSlots() {
        return scheduleRepository.findByStatus(SlotStatus.AVAILABLE);
    }

    public Schedule updateSchedule(String slotId, Schedule scheduleDetails) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + slotId));
        
        schedule.setDoctorId(scheduleDetails.getDoctorId());
        schedule.setDate(scheduleDetails.getDate());
        schedule.setStartTime(scheduleDetails.getStartTime());
        schedule.setEndTime(scheduleDetails.getEndTime());
        
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(String slotId) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + slotId));
        scheduleRepository.delete(schedule);
    }

    public Schedule bookSlot(String slotId) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found with id: " + slotId));
        
        if (schedule.getStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException("Slot is already booked");
        }
        
        schedule.setStatus(SlotStatus.BOOKED);
        return scheduleRepository.save(schedule);
    }

    public Schedule releaseSlot(String slotId) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found with id: " + slotId));
        
        if (schedule.getStatus() == SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is already available");
        }
        
        schedule.setStatus(SlotStatus.AVAILABLE);
        return scheduleRepository.save(schedule);
    }
}