package com.example.myevent_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractRequest {
    String name;
    
    @NotNull
    UUID paymentIntentId;
    
    String rentalId;
    String customerId;
    String status;
}
