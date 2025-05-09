package com.example.myevent_be.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationRentalRequest {

    String locationId;
    String rentalId;
    Integer quantity;
} 