package com.example.myevent_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCreateRequest {

    String name;
    String description;
    String detail;
    String img;
    boolean event_format;
    boolean is_template;
    String online_link;
    String invitation_link;
    String eventType_id;

}
