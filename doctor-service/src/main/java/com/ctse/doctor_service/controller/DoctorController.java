package com.ctse.doctor_service.controller;

import com.ctse.doctor_service.dto.DoctorDto;
import com.ctse.doctor_service.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public DoctorDto createDoctor(@RequestBody DoctorDto doctorDto) {
        return doctorService.createDoctor(doctorDto);
    }

    @GetMapping
    public List<DoctorDto> getDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{doctorId}")
    public DoctorDto getDoctor(@PathVariable String doctorId) {
        return doctorService.getDoctor(doctorId);
    }

    @PutMapping("/{doctorId}")
    public DoctorDto updateDoctor(@PathVariable String doctorId, @RequestBody DoctorDto doctorDto) {
        return doctorService.updateDoctor(doctorId, doctorDto);
    }

    @DeleteMapping("/{doctorId}")
    public void deleteDoctor(@PathVariable String doctorId) {
        doctorService.deleteDoctor(doctorId);
    }
}