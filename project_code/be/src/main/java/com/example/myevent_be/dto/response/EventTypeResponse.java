package com.example.myevent_be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventTypeResponse {
    String id;
    String name;
    Date created_at;
    Date update_at;
}
