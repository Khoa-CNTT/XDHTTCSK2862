package com.example.myevent_be.dto.request;

import com.example.myevent_be.enums.ContractStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractRequest {
    String name; // Tên hợp đồng
    String customerName; // Tên khách hàng
    String customerPhone; // Số điện thoại khách hàng
    String address; // Địa chỉ đầy đủ
    String rentalId; // rentalId nếu cần liên kết
    String status; // Trạng thái hợp đồng
    String paymentIntentId;
}
