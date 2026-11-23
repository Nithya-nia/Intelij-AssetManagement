package com.assetmanagement.service;

import com.assetmanagement.dto.AssetHistoryDto;
import com.assetmanagement.dto.DashboardStatsDto;
import com.assetmanagement.model.Notification;
import com.assetmanagement.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final LaptopRepository laptopRepository;
    private final MobileRepository mobileRepository;
    private final SimCardRepository simCardRepository;
    private final SimAssignmentRepository simAssignmentRepository;
    private final NotificationRepository notificationRepository;
    private final AssetHistoryService assetHistoryService;

    public DashboardService(EmployeeRepository employeeRepository,
                            LaptopRepository laptopRepository,
                            MobileRepository mobileRepository,
                            SimCardRepository simCardRepository,
                            SimAssignmentRepository simAssignmentRepository,
                            NotificationRepository notificationRepository,
                            AssetHistoryService assetHistoryService) {
        this.employeeRepository = employeeRepository;
        this.laptopRepository = laptopRepository;
        this.mobileRepository = mobileRepository;
        this.simCardRepository = simCardRepository;
        this.simAssignmentRepository = simAssignmentRepository;
        this.notificationRepository = notificationRepository;
        this.assetHistoryService = assetHistoryService;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        // Employees
        stats.setTotalEmployees(employeeRepository.count());
        stats.setActiveEmployees(employeeRepository.findByStatus("Active").size());
        stats.setInactiveEmployees(employeeRepository.findByStatus("Inactive").size());

        // Laptops
        stats.setTotalLaptops(laptopRepository.count());
        stats.setAssignedLaptops(laptopRepository.countByStatus("Assigned"));
        stats.setAvailableLaptops(laptopRepository.countByStatus("Available"));
        stats.setUnderRepairLaptops(laptopRepository.countByStatus("Under Repair"));
        stats.setRetiredLaptops(laptopRepository.countByStatus("Retired"));

        // Mobiles
        stats.setTotalMobiles(mobileRepository.count());
        stats.setAssignedMobiles(mobileRepository.countByStatus("Assigned"));
        stats.setAvailableMobiles(mobileRepository.countByStatus("Available"));
        stats.setUnderRepairMobiles(mobileRepository.countByStatus("Under Repair"));
        stats.setRetiredMobiles(mobileRepository.countByStatus("Retired"));

        // SIMs
        stats.setTotalSims(simCardRepository.count());
        stats.setAssignedSims(simCardRepository.countByStatus("Assigned"));
        stats.setAvailableSims(simCardRepository.countByStatus("Available"));
        stats.setInactiveSims(simCardRepository.countByStatus("Inactive"));

        // Active SIM Assignments
        stats.setActiveSimAssignments(simAssignmentRepository.countByStatus("Active"));

        // Notifications
        List<Notification> alerts = notificationRepository.findByExpiryStatusNotOrderByRemainingDaysAsc("Normal");
        stats.setSimExpiryAlerts(alerts);

        long expiringSoon = alerts.stream().filter(n -> "Expiring Soon".equalsIgnoreCase(n.getExpiryStatus()) || "Expires Today".equalsIgnoreCase(n.getExpiryStatus())).count();
        long expired = alerts.stream().filter(n -> "Expired".equalsIgnoreCase(n.getExpiryStatus())).count();
        stats.setExpiringSoonSims(expiringSoon);
        stats.setExpiredSims(expired);

        // Recent items
        stats.setRecentEmployees(employeeRepository.findTop5ByOrderByCreatedAtDesc());
        List<AssetHistoryDto> allHistory = assetHistoryService.getAllAssetHistory();
        stats.setRecentAssignments(allHistory.stream().limit(5).collect(Collectors.toList()));

        return stats;
    }
}
