package com.example.myevent_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@Table(name = "users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String first_name;
    String last_name;
    String email;

    @Size(min = 8)
    String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    Role role;

    String avatar;
    String phone_number;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP) // Xác định kiểu thời gian
    @Column(updatable = false)
    Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date update_at;

    @OneToMany(mappedBy = "user")
    Set<Token> tokens;

    @OneToMany(mappedBy = "id")
    Set<Device> devices;

    @OneToMany(mappedBy = "id")
    Set<Service> services;

    @OneToMany(mappedBy = "id")
    Set<Location> locations;
}
