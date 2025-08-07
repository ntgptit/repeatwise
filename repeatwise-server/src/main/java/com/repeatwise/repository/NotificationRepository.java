package com.repeatwise.repository;

import com.repeatwise.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Find notifications by user
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    // Find unread notifications by user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);
    
    // Find notifications by user and type
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, String type);
    
    // Find notifications by user and priority
    List<Notification> findByUserIdAndPriorityOrderByCreatedAtDesc(UUID userId, String priority);
    
    // Count unread notifications by user
    long countByUserIdAndIsReadFalse(UUID userId);
    
    // Find notifications scheduled to be sent
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt <= :now AND n.sentAt IS NULL AND n.deletedAt IS NULL")
    List<Notification> findNotificationsToSend(@Param("now") LocalDateTime now);
    
    // Find notifications by set
    List<Notification> findBySetIdOrderByCreatedAtDesc(UUID setId);
    
    // Find notifications by remind schedule
    List<Notification> findByRemindScheduleIdOrderByCreatedAtDesc(UUID remindScheduleId);
    
    // Find notifications created between dates
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndCreatedAtBetween(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Mark notifications as read
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") UUID userId);
    
    // Delete old notifications (older than specified days)
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
} 
