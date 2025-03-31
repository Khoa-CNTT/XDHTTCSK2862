package com.example.myevent_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
@Table(name = "device")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;
    String image;
    BigDecimal hourly_rental_fee;
    int quantity;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date update_at;

    @OneToMany(mappedBy = "device")
    Set<DeviceRental> deviceRentals;

    @ManyToOne
    @JoinColumn(name = "device_type_id", referencedColumnName = "id", nullable = false)
    Device_Type device_type;
}
