package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimelineUpdateRequest {

    Date time_start;
    String description;
    String rental_id;
}
