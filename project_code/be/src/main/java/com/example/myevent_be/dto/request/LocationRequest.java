package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationRequest {
    String name;
    String description;
    String image;
    BigDecimal hourly_rental_fee;
    String address;
    String userID;
}
