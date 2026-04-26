package com.ctse.patient_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String patientName;
    private String contactNumber;
    private String age;
    private String email;
    private String passwordHash; // In a real app this should be password for request, ignored in response
    private String role;
}
