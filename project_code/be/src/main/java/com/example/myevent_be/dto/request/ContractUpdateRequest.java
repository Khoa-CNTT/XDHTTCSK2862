package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractUpdateRequest {
    UUID contractId;
    String name;
    UUID paymentIntentId;
    String rentalId;
    String customerId;
    String status;
}
