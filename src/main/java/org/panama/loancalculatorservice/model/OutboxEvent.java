package org.panama.loancalculatorservice.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id",
    nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type",
    nullable = false)
    private String eventType;

    @Column(nullable = false,
    columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at",
    nullable = false)
    private LocalDateTime createdAt;
}
