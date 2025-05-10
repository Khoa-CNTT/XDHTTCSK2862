package com.example.myevent_be.repository;

import com.example.myevent_be.entity.ServiceRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRentalRepository extends JpaRepository<ServiceRental, String> {

    List<ServiceRental> findByRentalId(String rentalId);
} 