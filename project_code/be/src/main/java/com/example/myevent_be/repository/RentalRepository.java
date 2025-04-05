package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental,String> {
}
