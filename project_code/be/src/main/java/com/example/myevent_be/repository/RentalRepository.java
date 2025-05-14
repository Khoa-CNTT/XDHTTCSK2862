package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Rental;
import com.example.myevent_be.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
    List<Rental> findByEventId(String eventId);
    List<Rental> findByUserId(String userId);
}
