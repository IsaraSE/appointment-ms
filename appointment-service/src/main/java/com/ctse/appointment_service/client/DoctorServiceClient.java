package com.ctse.appointment_service.client;

import com.ctse.appointment_service.dto.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for synchronous communication with the Doctor Service.
 * Used to verify that a doctor exists before booking a slot against them.
 */
@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {

    @GetMapping("/doctors/{doctorId}")
    DoctorDto getDoctorById(@PathVariable("doctorId") String doctorId);
}
