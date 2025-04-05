package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractRequest {
    String name;
    UUID paymentIntentId;
    String rentalId;
    String customerId;
    String status;
}
