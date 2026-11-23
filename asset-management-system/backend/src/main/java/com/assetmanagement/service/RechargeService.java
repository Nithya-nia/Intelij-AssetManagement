package com.assetmanagement.service;

import com.assetmanagement.dto.RechargeRequest;
import com.assetmanagement.exception.BusinessRuleException;
import com.assetmanagement.exception.ResourceNotFoundException;
import com.assetmanagement.model.Recharge;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.repository.RechargeRepository;
import com.assetmanagement.repository.SimCardRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RechargeService {

    private final RechargeRepository rechargeRepository;
    private final SimCardRepository simCardRepository;
    private final NotificationSchedulerService notificationSchedulerService;

    public RechargeService(RechargeRepository rechargeRepository,
                           SimCardRepository simCardRepository,
                           @Lazy NotificationSchedulerService notificationSchedulerService) {
        this.rechargeRepository = rechargeRepository;
        this.simCardRepository = simCardRepository;
        this.notificationSchedulerService = notificationSchedulerService;
    }

    public List<Recharge> getAllRecharges() {
        return rechargeRepository.findAllByOrderByRechargeDateDesc();
    }

    public Recharge addRecharge(RechargeRequest request) {
        // Validation: Amount > 0
        if (request.getRechargeAmount() == null || request.getRechargeAmount() <= 0) {
            throw new BusinessRuleException("Recharge amount must be greater than zero.");
        }

        // Validation: Plan end date must be >= recharge date
        if (request.getPlanEndDate().isBefore(request.getRechargeDate())) {
            throw new BusinessRuleException("Plan end date (" + request.getPlanEndDate() +
                    ") must be equal to or later than recharge date (" + request.getRechargeDate() + ").");
        }

        // Lookup SIM Card
        SimCard sim = simCardRepository.findBySimId(request.getSimId())
                .or(() -> simCardRepository.findById(request.getSimId()))
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with ID: " + request.getSimId()));

        // Automatically populated from SIM
        String mobileNumber = sim.getMobileNumber();
        String provider = sim.getProvider();
        String employeeId = sim.getAssignedEmployeeId();
        String employeeName = sim.getAssignedEmployeeName() != null ? sim.getAssignedEmployeeName() : "Unassigned";

        // Save Recharge record (never overwrite old records)
        Recharge recharge = new Recharge(
                sim.getSimId(),
                mobileNumber,
                provider,
                employeeId,
                employeeName,
                request.getRechargeAmount(),
                request.getRechargePlan(),
                request.getRechargeDate(),
                request.getPlanEndDate(),
                request.getRemarks()
        );
        Recharge savedRecharge = rechargeRepository.save(recharge);

        // Update SIM's latest plan validity
        sim.setLatestRechargeAmount(request.getRechargeAmount());
        sim.setLatestRechargePlan(request.getRechargePlan());
        sim.setLatestRechargeDate(request.getRechargeDate());
        sim.setLatestPlanEndDate(request.getPlanEndDate());
        simCardRepository.save(sim);

        // Recalculate expiry notifications so that alerts update immediately
        notificationSchedulerService.updateSimExpiryNotifications();

        return savedRecharge;
    }

    public List<Recharge> getRechargesBySimId(String simId) {
        return rechargeRepository.findBySimIdOrderByRechargeDateDesc(simId);
    }

    public List<Recharge> searchAndFilter(String mobileNumber, String provider, String employee, LocalDate fromDate, LocalDate toDate) {
        return rechargeRepository.findAllByOrderByRechargeDateDesc().stream()
                .filter(r -> {
                    boolean matchesMobile = (mobileNumber == null || mobileNumber.trim().isEmpty())
                            || (r.getMobileNumber() != null && r.getMobileNumber().contains(mobileNumber.trim()));
                    boolean matchesProvider = (provider == null || provider.trim().isEmpty() || "ALL".equalsIgnoreCase(provider))
                            || (r.getProvider() != null && r.getProvider().equalsIgnoreCase(provider.trim()));
                    boolean matchesEmployee = (employee == null || employee.trim().isEmpty())
                            || (r.getEmployeeName() != null && r.getEmployeeName().toLowerCase().contains(employee.toLowerCase().trim()))
                            || (r.getEmployeeId() != null && r.getEmployeeId().toLowerCase().contains(employee.toLowerCase().trim()));
                    boolean matchesFrom = (fromDate == null) || (!r.getRechargeDate().isBefore(fromDate));
                    boolean matchesTo = (toDate == null) || (!r.getRechargeDate().isAfter(toDate));

                    return matchesMobile && matchesProvider && matchesEmployee && matchesFrom && matchesTo;
                })
                .collect(Collectors.toList());
    }
}
