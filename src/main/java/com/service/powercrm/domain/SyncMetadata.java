package com.service.powercrm.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "fipe_sync")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity", nullable = false, length = 50)
    private String entity;

    @Column(name = "last_sync", nullable = false)
    private Instant lastSync;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;
}
