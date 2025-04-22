package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {
}
