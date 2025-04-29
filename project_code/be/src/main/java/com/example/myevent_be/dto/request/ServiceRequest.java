package com.example.myevent_be.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRequest {
    String name;
    String description;
    String image;
    BigDecimal hourly_salary;
    int quantity;
    String place;
    String userID;
}
