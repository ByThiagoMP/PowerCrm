package com.service.powercrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service.powercrm.domain.SyncMetadata;

import java.util.Optional;

public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, Long> {
    Optional<SyncMetadata> findByEntity(String entity);
}
