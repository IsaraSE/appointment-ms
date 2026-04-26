package com.ctse.doctor_service.service;

import com.ctse.doctor_service.model.Doctor;
import com.ctse.doctor_service.repository.DoctorRepository;
import com.ctse.doctor_service.dto.DoctorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final RabbitTemplate rabbitTemplate;

    public DoctorService(DoctorRepository doctorRepository, RabbitTemplate rabbitTemplate) {
        this.doctorRepository = doctorRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DoctorDto createDoctor(DoctorDto doctorDto) {
        Doctor doctor = new Doctor(doctorDto.getName(), doctorDto.getSpecialization());
        Doctor savedDoctor = doctorRepository.save(doctor);

        try {
            rabbitTemplate.convertAndSend("appointment-exchange", "doctor.added", "Doctor Added: " + savedDoctor.getName());
        } catch (AmqpException e) {
            log.warn("Unable to publish doctor creation event to RabbitMQ: {}", e.getMessage());
        }

        return convertToDto(savedDoctor);
    }

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DoctorDto getDoctor(String doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
        return convertToDto(doctor);
    }

    public DoctorDto updateDoctor(String doctorId, DoctorDto updatedDoctorDto) {
        Doctor existing = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
        existing.setName(updatedDoctorDto.getName());
        existing.setSpecialization(updatedDoctorDto.getSpecialization());
        return convertToDto(doctorRepository.save(existing));
    }

    public void deleteDoctor(String doctorId) {
        Doctor existing = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
        doctorRepository.delete(existing);
    }

    private DoctorDto convertToDto(Doctor doctor) {
        return new DoctorDto(doctor.getDoctorId(), doctor.getName(), doctor.getSpecialization());
    }
}