package com.assetmanagement.service;

import com.assetmanagement.model.Notification;
import com.assetmanagement.model.Recharge;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.repository.NotificationRepository;
import com.assetmanagement.repository.RechargeRepository;
import com.assetmanagement.repository.SimCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSchedulerService.class);

    private final SimCardRepository simCardRepository;
    private final RechargeRepository rechargeRepository;
    private final NotificationRepository notificationRepository;

    public NotificationSchedulerService(SimCardRepository simCardRepository,
                                      RechargeRepository rechargeRepository,
                                      NotificationRepository notificationRepository) {
        this.simCardRepository = simCardRepository;
        this.rechargeRepository = rechargeRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Run daily at 1:00 AM or when configured
     */
    @Scheduled(cron = "${app.scheduling.sim-check-cron:0 0 1 * * ?}")
    public void runScheduledExpiryCheck() {
        logger.info("Executing scheduled SIM expiry check...");
        updateSimExpiryNotifications();
    }

    public synchronized void updateSimExpiryNotifications() {
        LocalDate today = LocalDate.now();
        List<SimCard> allSims = simCardRepository.findAll();

        for (SimCard sim : allSims) {
            // Find latest recharge for this SIM
            Optional<Recharge> latestRechargeOpt = rechargeRepository.findFirstBySimIdOrderByRechargeDateDesc(sim.getSimId());

            if (latestRechargeOpt.isPresent()) {
                Recharge recharge = latestRechargeOpt.get();
                LocalDate planEndDate = recharge.getPlanEndDate();

                long remainingDays = ChronoUnit.DAYS.between(today, planEndDate);
                String expiryStatus;

                if (remainingDays > 7) {
                    expiryStatus = "Normal";
                } else if (remainingDays >= 1) {
                    expiryStatus = "Expiring Soon";
                } else if (remainingDays == 0) {
                    expiryStatus = "Expires Today";
                } else {
                    expiryStatus = "Expired";
                }

                // Check if notification already exists for this SIM
                Optional<Notification> existingNotif = notificationRepository.findBySimId(sim.getSimId());
                Notification notif = existingNotif.orElse(new Notification());

                notif.setSimId(sim.getSimId());
                notif.setMobileNumber(sim.getMobileNumber());
                notif.setProvider(sim.getProvider());
                notif.setEmployeeName(sim.getAssignedEmployeeName() != null ? sim.getAssignedEmployeeName() : "Unassigned");
                notif.setRechargeAmount(recharge.getRechargeAmount());
                notif.setRechargeDate(recharge.getRechargeDate());
                notif.setPlanEndDate(planEndDate);
                notif.setRemainingDays(remainingDays);
                notif.setExpiryStatus(expiryStatus);
                notif.setCalculatedAt(LocalDateTime.now());

                notificationRepository.save(notif);

                // Also update latest recharge metadata on the SimCard entity
                sim.setLatestRechargePlan(recharge.getRechargePlan());
                sim.setLatestRechargeAmount(recharge.getRechargeAmount());
                sim.setLatestRechargeDate(recharge.getRechargeDate());
                sim.setLatestPlanEndDate(planEndDate);
                simCardRepository.save(sim);
            }
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByRemainingDaysAsc();
    }

    public List<Notification> getAlertNotifications() {
        // Exclude Normal status (show Expiring Soon, Expires Today, and Expired)
        return notificationRepository.findByExpiryStatusNotOrderByRemainingDaysAsc("Normal");
    }
}
