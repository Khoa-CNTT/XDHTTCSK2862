package com.example.myevent_be.dto.request;

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
    String provinceId;
    String districtId;
    String wardId;
    String street; // Số nhà, tên đường

    Date eventTime; // Thời gian tổ chức
    String eventAddress; // Địa chỉ đầy đủ (nếu FE chỉ gửi 1 trường)
    String rentalId; // rentalId nếu cần liên kết

    String status; // Trạng thái hợp đồng

    @NotNull
    UUID paymentIntentId;
}
