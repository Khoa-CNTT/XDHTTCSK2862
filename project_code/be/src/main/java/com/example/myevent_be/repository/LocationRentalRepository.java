package com.example.myevent_be.repository;

import com.example.myevent_be.entity.LocationRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRentalRepository extends JpaRepository<LocationRental, String> {
    List<LocationRental> findByRentalId(String rentalId);
} 