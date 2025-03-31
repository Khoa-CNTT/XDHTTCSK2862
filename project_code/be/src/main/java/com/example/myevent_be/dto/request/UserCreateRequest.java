package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    String first_name;
    String last_name;
    String email;
    String password;
    String phone_number;
}
