package com.assetmanagement.repository;

import com.assetmanagement.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByExpiryStatusNotOrderByRemainingDaysAsc(String expiryStatus);
    List<Notification> findAllByOrderByRemainingDaysAsc();
    Optional<Notification> findBySimId(String simId);
    void deleteBySimId(String simId);
}
