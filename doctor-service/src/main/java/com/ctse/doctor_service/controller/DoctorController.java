package com.ctse.doctor_service.controller;

import com.ctse.doctor_service.dto.DoctorDto;
import com.ctse.doctor_service.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@Tag(name = "Doctor Management", description = "APIs for managing doctor profiles")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @Operation(summary = "Register a new doctor", description = "Create a new doctor profile in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor created successfully",
                content = @Content(schema = @Schema(implementation = DoctorDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody DoctorDto doctorDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(doctorDto));
    }

    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieve the full directory of registered doctors")
    @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully")
    public ResponseEntity<List<DoctorDto>> getDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{doctorId}")
    @Operation(summary = "Get doctor by ID", description = "Retrieve a specific doctor's profile by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found",
                content = @Content(schema = @Schema(implementation = DoctorDto.class))),
        @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content)
    })
    public ResponseEntity<DoctorDto> getDoctor(
            @Parameter(description = "Doctor ID", required = true) @PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getDoctor(doctorId));
    }

    @PutMapping("/{doctorId}")
    @Operation(summary = "Update doctor", description = "Update an existing doctor's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor updated successfully",
                content = @Content(schema = @Schema(implementation = DoctorDto.class))),
        @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content)
    })
    public ResponseEntity<DoctorDto> updateDoctor(
            @Parameter(description = "Doctor ID", required = true) @PathVariable String doctorId,
            @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.updateDoctor(doctorId, doctorDto));
    }

    @DeleteMapping("/{doctorId}")
    @Operation(summary = "Delete doctor", description = "Remove a doctor from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content)
    })
    public ResponseEntity<Void> deleteDoctor(
            @Parameter(description = "Doctor ID", required = true) @PathVariable String doctorId) {
        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }
}