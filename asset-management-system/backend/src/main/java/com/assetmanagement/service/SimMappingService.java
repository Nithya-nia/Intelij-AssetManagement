package com.assetmanagement.service;

import com.assetmanagement.dto.SimMappingRequest;
import com.assetmanagement.dto.SimUnmapRequest;
import com.assetmanagement.exception.BusinessRuleException;
import com.assetmanagement.exception.ResourceNotFoundException;
import com.assetmanagement.model.Employee;
import com.assetmanagement.model.Mobile;
import com.assetmanagement.model.SimAssignment;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.repository.EmployeeRepository;
import com.assetmanagement.repository.MobileRepository;
import com.assetmanagement.repository.SimAssignmentRepository;
import com.assetmanagement.repository.SimCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimMappingService {

    private final MobileRepository mobileRepository;
    private final SimCardRepository simCardRepository;
    private final SimAssignmentRepository simAssignmentRepository;
    private final EmployeeRepository employeeRepository;

    public SimMappingService(MobileRepository mobileRepository,
                             SimCardRepository simCardRepository,
                             SimAssignmentRepository simAssignmentRepository,
                             EmployeeRepository employeeRepository) {
        this.mobileRepository = mobileRepository;
        this.simCardRepository = simCardRepository;
        this.simAssignmentRepository = simAssignmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Map<String, Object>> getSimMappingStatuses() {
        List<Mobile> mobiles = mobileRepository.findAll();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Mobile m : mobiles) {
            Map<String, Object> map = new HashMap<>();
            map.put("mobileId", m.getMobileId());
            map.put("id", m.getId());
            map.put("name", m.getName());
            map.put("brand", m.getBrand());
            map.put("imeiNumber", m.getImeiNumber());
            map.put("projectName", m.getProjectName());
            map.put("simSlotConfig", m.getSimSlotConfig() != null ? m.getSimSlotConfig() : "Dual SIM");
            map.put("status", m.getStatus());
            map.put("assignedEmployeeId", m.getAssignedEmployeeId());
            map.put("assignedEmployeeName", m.getAssignedEmployeeName());

            int maxSlots = "Single SIM".equalsIgnoreCase(m.getSimSlotConfig()) ? 1 : 2;
            map.put("maxSlots", maxSlots);

            // Slot 1 details
            Map<String, Object> s1 = new HashMap<>();
            s1.put("simId", m.getSlot1SimId());
            s1.put("mobileNumber", m.getSlot1MobileNumber());
            s1.put("provider", m.getSlot1Provider());
            s1.put("isOccupied", m.getSlot1SimId() != null && !m.getSlot1SimId().isEmpty());
            map.put("slot1", s1);

            // Slot 2 details
            Map<String, Object> s2 = new HashMap<>();
            s2.put("simId", m.getSlot2SimId());
            s2.put("mobileNumber", m.getSlot2MobileNumber());
            s2.put("provider", m.getSlot2Provider());
            s2.put("isOccupied", m.getSlot2SimId() != null && !m.getSlot2SimId().isEmpty());
            s2.put("isSupported", maxSlots >= 2);
            map.put("slot2", s2);

            int occupied = 0;
            if (m.getSlot1SimId() != null && !m.getSlot1SimId().isEmpty()) occupied++;
            if (maxSlots >= 2 && m.getSlot2SimId() != null && !m.getSlot2SimId().isEmpty()) occupied++;

            map.put("occupiedSlots", occupied);
            map.put("freeSlots", Math.max(0, maxSlots - occupied));

            results.add(map);
        }

        return results;
    }

    public List<SimCard> getAvailableSimsForMapping() {
        return simCardRepository.findAll().stream()
                .filter(s -> ("Available".equalsIgnoreCase(s.getStatus()) || s.getStatus() == null)
                        && (s.getAssignedMobileId() == null || s.getAssignedMobileId().isEmpty()))
                .collect(Collectors.toList());
    }

    public Mobile mapSimToMobile(SimMappingRequest request) {
        Mobile mobile = mobileRepository.findByMobileId(request.getMobileId())
                .or(() -> mobileRepository.findById(request.getMobileId()))
                .orElseThrow(() -> new ResourceNotFoundException("Mobile not found with ID: " + request.getMobileId()));

        SimCard sim = simCardRepository.findBySimId(request.getSimId())
                .or(() -> simCardRepository.findById(request.getSimId()))
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with ID: " + request.getSimId()));

        if (sim.getAssignedMobileId() != null && !sim.getAssignedMobileId().isEmpty()
                && !sim.getAssignedMobileId().equalsIgnoreCase(mobile.getMobileId())) {
            throw new BusinessRuleException("SIM " + sim.getMobileNumber() + " is already mapped to mobile " + sim.getAssignedMobileId());
        }

        String slotConfig = mobile.getSimSlotConfig() != null ? mobile.getSimSlotConfig() : "Dual SIM";
        int maxSlots = "Single SIM".equalsIgnoreCase(slotConfig) ? 1 : 2;

        int targetSlot = request.getSlotNumber() != null ? request.getSlotNumber() : 1;
        if (targetSlot < 1 || targetSlot > maxSlots) {
            throw new BusinessRuleException("Invalid slot " + targetSlot + " for a " + slotConfig + " phone. Max slot is " + maxSlots);
        }

        if (targetSlot == 1 && mobile.getSlot1SimId() != null && !mobile.getSlot1SimId().isEmpty()) {
            throw new BusinessRuleException("Slot 1 is already occupied by " + mobile.getSlot1MobileNumber() + " (" + mobile.getSlot1Provider() + ")");
        }

        if (targetSlot == 2 && mobile.getSlot2SimId() != null && !mobile.getSlot2SimId().isEmpty()) {
            throw new BusinessRuleException("Slot 2 is already occupied by " + mobile.getSlot2MobileNumber() + " (" + mobile.getSlot2Provider() + ")");
        }

        // If mobile is assigned to an employee, handle employee SIM assignment constraints
        if ("Assigned".equalsIgnoreCase(mobile.getStatus()) && mobile.getAssignedEmployeeId() != null) {
            Employee employee = employeeRepository.findByEmployeeId(mobile.getAssignedEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned employee not found with ID: " + mobile.getAssignedEmployeeId()));

            if ("Inactive".equalsIgnoreCase(employee.getStatus())) {
                throw new BusinessRuleException("Cannot map SIM to mobile assigned to inactive employee " + employee.getName());
            }

            List<SimAssignment> activeEmployeeSims = simAssignmentRepository.findByEmployeeIdAndStatus(employee.getEmployeeId(), "Active");
            if (activeEmployeeSims.size() >= 2) {
                throw new BusinessRuleException("Employee " + employee.getName() + " already has the maximum of 2 active SIM cards assigned.");
            }

            // Create assignment record
            SimAssignment assignment = new SimAssignment(
                    sim.getSimId(),
                    sim.getSimId(),
                    sim.getMobileNumber(),
                    sim.getProvider(),
                    employee.getEmployeeId(),
                    employee.getName(),
                    mobile.getMobileId(),
                    mobile.getName(),
                    LocalDate.now(),
                    request.getRemarks() != null ? request.getRemarks() : "Mapped to slot " + targetSlot
            );
            assignment.setSlotNumber(targetSlot);
            simAssignmentRepository.save(assignment);

            // Update SIM
            sim.setStatus("Assigned");
            sim.setAssignedEmployeeId(employee.getEmployeeId());
            sim.setAssignedEmployeeName(employee.getName());
        } else {
            // Mobile is Available, SIM remains available but linked to this hardware
            sim.setStatus("Available");
        }

        sim.setAssignedMobileId(mobile.getMobileId());
        sim.setAssignedMobileName(mobile.getName());
        sim.setUpdatedAt(LocalDateTime.now());
        simCardRepository.save(sim);

        // Update mobile slot mapping
        if (targetSlot == 1) {
            mobile.setSlot1SimId(sim.getSimId());
            mobile.setSlot1MobileNumber(sim.getMobileNumber());
            mobile.setSlot1Provider(sim.getProvider());
        } else {
            mobile.setSlot2SimId(sim.getSimId());
            mobile.setSlot2MobileNumber(sim.getMobileNumber());
            mobile.setSlot2Provider(sim.getProvider());
        }

        int count = 0;
        if (mobile.getSlot1SimId() != null && !mobile.getSlot1SimId().isEmpty()) count++;
        if (mobile.getSlot2SimId() != null && !mobile.getSlot2SimId().isEmpty()) count++;
        mobile.setActiveSimCount(count);
        mobile.setUpdatedAt(LocalDateTime.now());

        return mobileRepository.save(mobile);
    }

    public Mobile unmapSimFromMobile(SimUnmapRequest request) {
        Mobile mobile = mobileRepository.findByMobileId(request.getMobileId())
                .or(() -> mobileRepository.findById(request.getMobileId()))
                .orElseThrow(() -> new ResourceNotFoundException("Mobile not found with ID: " + request.getMobileId()));

        int targetSlot = 0;
        String simIdToUnmap = null;

        if (request.getSlotNumber() != null) {
            targetSlot = request.getSlotNumber();
            if (targetSlot == 1) {
                simIdToUnmap = mobile.getSlot1SimId();
            } else if (targetSlot == 2) {
                simIdToUnmap = mobile.getSlot2SimId();
            }
        } else if (request.getSimId() != null) {
            if (request.getSimId().equalsIgnoreCase(mobile.getSlot1SimId())) {
                targetSlot = 1;
                simIdToUnmap = mobile.getSlot1SimId();
            } else if (request.getSimId().equalsIgnoreCase(mobile.getSlot2SimId())) {
                targetSlot = 2;
                simIdToUnmap = mobile.getSlot2SimId();
            }
        }

        if (targetSlot == 0 || simIdToUnmap == null || simIdToUnmap.isEmpty()) {
            throw new BusinessRuleException("No SIM found in the specified slot to unmap.");
        }

        // Unmap from Mobile
        if (targetSlot == 1) {
            mobile.setSlot1SimId(null);
            mobile.setSlot1MobileNumber(null);
            mobile.setSlot1Provider(null);
        } else if (targetSlot == 2) {
            mobile.setSlot2SimId(null);
            mobile.setSlot2MobileNumber(null);
            mobile.setSlot2Provider(null);
        }

        int count = 0;
        if (mobile.getSlot1SimId() != null && !mobile.getSlot1SimId().isEmpty()) count++;
        if (mobile.getSlot2SimId() != null && !mobile.getSlot2SimId().isEmpty()) count++;
        mobile.setActiveSimCount(count);
        mobile.setUpdatedAt(LocalDateTime.now());
        Mobile savedMobile = mobileRepository.save(mobile);

        // Update SIM Card and any active assignment
        final String searchSimId = simIdToUnmap;
        Optional<SimCard> simOpt = simCardRepository.findBySimId(searchSimId)
                .or(() -> simCardRepository.findById(searchSimId));
        if (simOpt.isPresent()) {
            SimCard sim = simOpt.get();

            // Check if there was an active assignment
            Optional<SimAssignment> activeAssignment = simAssignmentRepository.findBySimIdAndStatus(sim.getSimId(), "Active");
            if (activeAssignment.isPresent()) {
                SimAssignment sa = activeAssignment.get();
                sa.setStatus("Returned");
                sa.setReturnedDate(LocalDate.now());
                sa.setRemarks((sa.getRemarks() != null ? sa.getRemarks() + " | Unmapped: " : "Unmapped from device: ") +
                        (request.getRemarks() != null ? request.getRemarks() : "Manual unmap"));
                simAssignmentRepository.save(sa);
            }

            sim.setStatus("Available");
            sim.setAssignedEmployeeId(null);
            sim.setAssignedEmployeeName(null);
            sim.setAssignedMobileId(null);
            sim.setAssignedMobileName(null);
            sim.setUpdatedAt(LocalDateTime.now());
            simCardRepository.save(sim);
        }

        return savedMobile;
    }
}
