package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    boolean existsByName(String name);
    Optional<Event> findByName(String event);

}

