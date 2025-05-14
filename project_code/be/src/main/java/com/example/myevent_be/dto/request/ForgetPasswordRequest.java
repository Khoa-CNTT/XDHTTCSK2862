package com.example.myevent_be.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordRequest {

    @Email(message = "Email không hợp lệ")
    String email;
    String code;
//    String newPassword;
//    String confirmPassword;
}
