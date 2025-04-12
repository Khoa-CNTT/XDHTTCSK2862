package com.example.myevent_be.repository;


import com.example.myevent_be.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, String> {
    boolean existsByName(String name);

    Optional<EventType> findByName(String eventType);
}
