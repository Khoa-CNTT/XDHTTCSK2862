package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRequest {
    String name;
    String description;
    String image;
    BigDecimal hourlyRentalFee;
    int quantity;
    String deviceType_id;
    String place;
    String userID;
}