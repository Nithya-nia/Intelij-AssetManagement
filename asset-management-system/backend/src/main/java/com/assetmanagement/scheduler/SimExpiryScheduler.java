package com.assetmanagement.scheduler;

import com.assetmanagement.service.NotificationSchedulerService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SimExpiryScheduler {

    private final NotificationSchedulerService notificationSchedulerService;

    public SimExpiryScheduler(NotificationSchedulerService notificationSchedulerService) {
        this.notificationSchedulerService = notificationSchedulerService;
    }
}
