package com.ctse.doctor_service.controller;

import com.ctse.doctor_service.model.Doctor;
import com.ctse.doctor_service.dto.DoctorDto;
import com.ctse.doctor_service.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public DoctorDto createDoctor(@RequestBody DoctorDto doctorDto) {
        Doctor doctor = new Doctor(doctorDto.getName(), doctorDto.getSpecialization());
        Doctor saved = doctorService.createDoctor(doctor);
        return convertToDto(saved);
    }

    @GetMapping
    public List<DoctorDto> getDoctors() {
        return doctorService.getAllDoctors().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{doctorId}")
    public DoctorDto getDoctor(@PathVariable String doctorId) {
        return convertToDto(doctorService.getDoctor(doctorId));
    }

    @PutMapping("/{doctorId}")
    public DoctorDto updateDoctor(@PathVariable String doctorId, @RequestBody DoctorDto doctorDto) {
        Doctor doctor = new Doctor(doctorDto.getName(), doctorDto.getSpecialization());
        return convertToDto(doctorService.updateDoctor(doctorId, doctor));
    }

    @DeleteMapping("/{doctorId}")
    public void deleteDoctor(@PathVariable String doctorId) {
        doctorService.deleteDoctor(doctorId);
    }

    private DoctorDto convertToDto(Doctor doctor) {
        return new DoctorDto(doctor.getDoctorId(), doctor.getName(), doctor.getSpecialization());
    }
}