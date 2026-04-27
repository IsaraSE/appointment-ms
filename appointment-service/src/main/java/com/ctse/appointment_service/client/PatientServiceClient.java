package com.ctse.appointment_service.client;

import com.ctse.appointment_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for synchronous communication with the Patient Service.
 * Used to verify that a patient (user) exists before confirming a slot booking.
 */
@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    @GetMapping("/auth/users/{userId}")
    UserDto getUserById(@PathVariable("userId") String userId);
}
