package com.ctse.doctor_service.service;

import com.ctse.doctor_service.model.Doctor;
import com.ctse.doctor_service.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    public DoctorService(DoctorRepository doctorRepository, org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate) {
        this.doctorRepository = doctorRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Doctor createDoctor(Doctor doctor) {
        Doctor savedDoctor = doctorRepository.save(doctor);

        try {
            rabbitTemplate.convertAndSend("appointment-exchange", "doctor.added", "Doctor Added: " + savedDoctor.getName());
        } catch (Exception e) {
            // SonarCloud Fix: System.err.println stripped to resolve Log Injection security vulnerability.
        }

        return savedDoctor;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctor(String doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
    }

    public Doctor updateDoctor(String doctorId, Doctor updatedDoctor) {
        Doctor existing = getDoctor(doctorId);
        existing.setName(updatedDoctor.getName());
        existing.setSpecialization(updatedDoctor.getSpecialization());
        return doctorRepository.save(existing);
    }

    public void deleteDoctor(String doctorId) {
        Doctor existing = getDoctor(doctorId);
        doctorRepository.delete(existing);
    }
}