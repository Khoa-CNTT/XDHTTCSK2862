package com.example.myevent_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;
    String detail;
    String img;
    boolean event_format;
    boolean is_template;
    String online_link;
    String invitation_link;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date update_at;

    @ManyToOne
    @JoinColumn(name = "eventType_id", referencedColumnName = "id", nullable = false)
    EventType event_type;

    @OneToMany(mappedBy = "event")
    Set<Rental> rentals;
}
