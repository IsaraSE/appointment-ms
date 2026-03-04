package com.ctse.booking_service.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private String userId;
    private String slotId;
}
