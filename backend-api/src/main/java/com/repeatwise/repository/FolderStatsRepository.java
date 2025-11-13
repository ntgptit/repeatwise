package com.repeatwise.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.FolderStats;

/**
 * Repository for cached folder statistics.
 */
@Repository
public interface FolderStatsRepository extends JpaRepository<FolderStats, UUID> {

    Optional<FolderStats> findByFolderIdAndUserId(UUID folderId, UUID userId);
}

