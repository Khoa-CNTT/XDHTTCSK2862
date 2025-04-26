package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, String> {
}
