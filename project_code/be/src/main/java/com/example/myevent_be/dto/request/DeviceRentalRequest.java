package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRentalRequest {
    String deviceId;
    String rentalId;
    Integer quantity;

}