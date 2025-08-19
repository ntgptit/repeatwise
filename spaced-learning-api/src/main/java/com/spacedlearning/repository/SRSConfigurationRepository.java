package com.spacedlearning.repository;

import com.spacedlearning.entity.SRSConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SRSConfigurationRepository extends JpaRepository<SRSConfiguration, UUID> {

    // Find active configuration
    Optional<SRSConfiguration> findByIsActiveTrue();
    
    // Find configuration by description
    Optional<SRSConfiguration> findByDescription(String description);
    
    // Check if there's an active configuration
    boolean existsByIsActiveTrue();
}
