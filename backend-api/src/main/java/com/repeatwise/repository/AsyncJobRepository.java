package com.repeatwise.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.AsyncJob;
import com.repeatwise.enums.AsyncJobType;

/**
 * Repository cho bảng async_jobs.
 */
@Repository
public interface AsyncJobRepository extends JpaRepository<AsyncJob, UUID> {

    Optional<AsyncJob> findByIdAndUserId(UUID id, UUID userId);

    Optional<AsyncJob> findByIdAndUserIdAndJobType(UUID id, UUID userId, AsyncJobType jobType);
}

