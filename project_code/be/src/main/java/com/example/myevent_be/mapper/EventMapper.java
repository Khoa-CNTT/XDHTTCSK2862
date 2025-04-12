package com.example.myevent_be.mapper;

import com.example.myevent_be.dto.request.EventCreateRequest;
import com.example.myevent_be.dto.request.EventUpdateRequest;
import com.example.myevent_be.dto.response.EventResponse;
import com.example.myevent_be.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EventTypeMapper.class)
public interface EventMapper {
    @Mapping(source = "eventType_id", target = "event_type.id")
    Event toEvent(EventCreateRequest request);

//    @Mapping(source = "event_type.id", target = "eventType_id") // 👈 Map khi trả về
    @Mapping(source = "event_type", target = "eventTypeName", qualifiedByName = "eventTypeToString")
    EventResponse toEventResponse(Event event);

    void updateEvent(@MappingTarget Event event, EventUpdateRequest request);
}
