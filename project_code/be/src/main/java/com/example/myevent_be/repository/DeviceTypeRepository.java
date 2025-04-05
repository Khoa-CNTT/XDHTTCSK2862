package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Device_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeRepository extends JpaRepository<Device_Type,String> {
    boolean existsByName(String name);
}
