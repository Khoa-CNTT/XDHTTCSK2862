package com.example.myevent_be.repository;

import com.example.myevent_be.entity.LocationRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRentalRepository extends JpaRepository<LocationRental, String> {
} 