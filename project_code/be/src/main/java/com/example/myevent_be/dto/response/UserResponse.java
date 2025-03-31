package com.example.myevent_be.dto.response;

import com.example.myevent_be.entity.Role;
import com.example.myevent_be.entity.Token;
import com.example.myevent_be.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;

    String first_name;
    String last_name;
    String email;
    String avatar;
    String phone_number;
    Date created_at;
    Date update_at;
    String roleName; // Chỉ lấy tên role

}
