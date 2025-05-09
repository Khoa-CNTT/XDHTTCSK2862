package com.example.myevent_be.repository;

import com.example.myevent_be.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimelineRepository extends JpaRepository<TimeLine, String> {

    List<TimeLine> findByRentalId(String rentalId);
}