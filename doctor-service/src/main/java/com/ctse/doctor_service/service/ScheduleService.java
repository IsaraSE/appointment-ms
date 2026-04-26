package com.ctse.doctor_service.service;

import com.ctse.doctor_service.model.Schedule;
import com.ctse.doctor_service.model.SlotStatus;
import com.ctse.doctor_service.repository.ScheduleRepository;
import com.ctse.doctor_service.dto.ScheduleDto;
import com.ctse.doctor_service.exception.ResourceNotFoundException;
import com.ctse.doctor_service.exception.ResourceConflictException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        schedule.setDoctorId(scheduleDto.getDoctorId());
        schedule.setDate(scheduleDto.getDate());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setStatus(SlotStatus.AVAILABLE);
        return convertToDto(scheduleRepository.save(schedule));
    }

    public List<ScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<ScheduleDto> getAvailableSlots() {
        return scheduleRepository.findByStatus(SlotStatus.AVAILABLE).stream()
                .map(this::convertToDto)
                .toList();
    }

    public ScheduleDto updateSchedule(String slotId, ScheduleDto scheduleDetails) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + slotId));
        
        schedule.setDoctorId(scheduleDetails.getDoctorId());
        schedule.setDate(scheduleDetails.getDate());
        schedule.setStartTime(scheduleDetails.getStartTime());
        schedule.setEndTime(scheduleDetails.getEndTime());
        
        return convertToDto(scheduleRepository.save(schedule));
    }

    public void deleteSchedule(String slotId) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + slotId));
        scheduleRepository.delete(schedule);
    }

    public ScheduleDto bookSlot(String slotId) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));
        
        if (schedule.getStatus() == SlotStatus.BOOKED) {
            throw new ResourceConflictException("Slot is already booked");
        }
        
        schedule.setStatus(SlotStatus.BOOKED);
        return convertToDto(scheduleRepository.save(schedule));
    }

    public ScheduleDto releaseSlot(String slotId) {
        Schedule schedule = scheduleRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));
        
        if (schedule.getStatus() == SlotStatus.AVAILABLE) {
            throw new ResourceConflictException("Slot is already available");
        }
        
        schedule.setStatus(SlotStatus.AVAILABLE);
        return convertToDto(scheduleRepository.save(schedule));
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