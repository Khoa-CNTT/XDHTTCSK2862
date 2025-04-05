package com.example.myevent_be.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {
    @NotNull(message = "ID cannot be null")
    String id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    String name;

    @NotNull(message = "Payment Intent ID cannot be null")
    UUID paymentIntentId;

    String rentalId;
    String customerId;

    @NotNull(message = "Status cannot be null")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    String status;
}
