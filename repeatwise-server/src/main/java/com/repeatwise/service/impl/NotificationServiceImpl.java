package com.repeatwise.service.impl;

import com.repeatwise.dto.NotificationDto;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.NotificationMapper;
import com.repeatwise.model.Notification;
import com.repeatwise.model.User;
import com.repeatwise.repository.NotificationRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.NotificationService;
import com.repeatwise.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        ServiceUtils.logOperationStart("notification creation", notificationDto.getUserId());
        
        User user = ServiceUtils.findEntityOrThrow(
                () -> userRepository.findById(notificationDto.getUserId()), 
                "User", 
                notificationDto.getUserId()
        );
        
        Notification notification = notificationMapper.toEntity(notificationDto);
        notification.setUser(user);
        
        Notification savedNotification = notificationRepository.save(notification);
        ServiceUtils.logOperationSuccess("notification creation", savedNotification.getId());
        
        return notificationMapper.toDto(savedNotification);
    }

    @Override
    public Optional<NotificationDto> findById(UUID id) {
        ServiceUtils.logEntityLookup("notification", id);
        return notificationRepository.findById(id)
                .map(notificationMapper::toDto);
    }

    @Override
    public List<NotificationDto> findByUserId(UUID userId) {
        ServiceUtils.logEntityLookup("notifications for user", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    public List<NotificationDto> findUnreadByUserId(UUID userId) {
        ServiceUtils.logEntityLookup("unread notifications for user", userId);
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    public NotificationDto markAsRead(UUID id, UUID userId) {
        ServiceUtils.logOperationStart("notification mark as read", id, userId);
        
        Notification notification = ServiceUtils.findEntityOrThrow(
                () -> notificationRepository.findById(id), 
                "Notification", 
                id
        );
        
        // Verify user owns this notification
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + id + " for user ID: " + userId);
        }
        
        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        
        ServiceUtils.logOperationSuccess("notification mark as read", updatedNotification.getId());
        return notificationMapper.toDto(updatedNotification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        ServiceUtils.logOperationStart("mark all notifications as read", userId);
        notificationRepository.markAllAsReadByUserId(userId);
        ServiceUtils.logOperationSuccess("mark all notifications as read", userId);
    }

    @Override
    public void deleteNotification(UUID id, UUID userId) {
        ServiceUtils.logOperationStart("notification deletion", id, userId);
        
        Notification notification = ServiceUtils.findEntityOrThrow(
                () -> notificationRepository.findById(id), 
                "Notification", 
                id
        );
        
        // Verify user owns this notification
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + id + " for user ID: " + userId);
        }
        
        notificationRepository.delete(notification);
        ServiceUtils.logOperationSuccess("notification deletion", id);
    }

    @Override
    public List<NotificationDto> sendScheduledNotifications() {
        ServiceUtils.logOperationStart("scheduled notifications sending");
        
        List<Notification> notificationsToSend = notificationRepository.findNotificationsToSend(LocalDateTime.now());
        
        for (Notification notification : notificationsToSend) {
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Notification sent: {}", notification.getTitle());
        }
        
        ServiceUtils.logOperationSuccess("scheduled notifications sending", notificationsToSend.size());
        return notificationMapper.toDtoList(notificationsToSend);
    }

    @Override
    public NotificationDto createReviewDueNotification(UUID userId, UUID setId, String setName) {
        ServiceUtils.logOperationStart("review due notification creation", userId, setName);
        
        NotificationDto notificationDto = NotificationDto.builder()
                .userId(userId)
                .setId(setId)
                .title("Review Due")
                .message("Time to review your set: " + setName)
                .type("REVIEW_DUE")
                .priority("HIGH")
                .isRead(false)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        return createNotification(notificationDto);
    }

    @Override
    public NotificationDto createCycleCompletedNotification(UUID userId, UUID setId, String setName, int cycleNo) {
        ServiceUtils.logOperationStart("cycle completed notification creation", userId, setName);
        
        NotificationDto notificationDto = NotificationDto.builder()
                .userId(userId)
                .setId(setId)
                .title("Cycle Completed")
                .message("Congratulations! You completed cycle " + cycleNo + " for set: " + setName)
                .type("CYCLE_COMPLETED")
                .priority("MEDIUM")
                .isRead(false)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        return createNotification(notificationDto);
    }

    @Override
    public NotificationDto createSetMasteredNotification(UUID userId, UUID setId, String setName) {
        ServiceUtils.logOperationStart("set mastered notification creation", userId, setName);
        
        NotificationDto notificationDto = NotificationDto.builder()
                .userId(userId)
                .setId(setId)
                .title("Set Mastered")
                .message("Excellent! You have mastered the set: " + setName)
                .type("SET_MASTERED")
                .priority("HIGH")
                .isRead(false)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        return createNotification(notificationDto);
    }

    @Override
    public NotificationStatistics getNotificationStatistics(UUID userId) {
        ServiceUtils.logEntityLookup("notification statistics", userId);
        
        long totalNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
        long unreadNotifications = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long highPriorityNotifications = notificationRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, "HIGH").size();
        
        LocalDateTime lastNotificationDate = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .findFirst()
                .map(Notification::getCreatedAt)
                .orElse(null);
        
        return new NotificationStatistics(
                totalNotifications,
                unreadNotifications,
                highPriorityNotifications,
                lastNotificationDate
        );
    }
} 
