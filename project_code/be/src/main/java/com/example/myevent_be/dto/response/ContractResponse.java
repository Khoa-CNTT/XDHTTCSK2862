package com.example.myevent_be.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {

    String id;
    String name;
    String customerName;
    String customerPhone;
    String address;
    String status;
    String paymentIntentId;
    String rentalId;
    Date createdAt;
    Date updatedAt;
}
