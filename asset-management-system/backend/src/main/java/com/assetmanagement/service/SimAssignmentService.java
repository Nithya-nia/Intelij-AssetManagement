package com.assetmanagement.service;

import com.assetmanagement.dto.SimAssignRequest;
import com.assetmanagement.dto.SimReturnRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SimAssignmentService {

    private final SimCardRepository simCardRepository;
    private final SimAssignmentRepository simAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final MobileRepository mobileRepository;

    public SimAssignmentService(SimCardRepository simCardRepository,
                                SimAssignmentRepository simAssignmentRepository,
                                EmployeeRepository employeeRepository,
                                MobileRepository mobileRepository) {
        this.simCardRepository = simCardRepository;
        this.simAssignmentRepository = simAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.mobileRepository = mobileRepository;
    }

    public List<SimAssignment> getAllAssignments() {
        return simAssignmentRepository.findAllByOrderByCreatedAtDesc();
    }

    public SimAssignment assignSim(String simId, SimAssignRequest request) {
        SimCard sim = simCardRepository.findBySimId(simId)
                .or(() -> simCardRepository.findById(simId))
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with ID: " + simId));

        // Rule 7: One SIM card can be assigned to only one employee at a time. Must be Available
        if (!"Available".equalsIgnoreCase(sim.getStatus())) {
            throw new BusinessRuleException("Cannot assign SIM card. Current status is " + sim.getStatus() +
                    ". Only 'Available' SIM cards can be assigned.");
        }

        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        // Rule 11: Inactive employees cannot receive new assets
        if ("Inactive".equalsIgnoreCase(employee.getStatus())) {
            throw new BusinessRuleException("Cannot assign SIM card to inactive employee: " + employee.getName() + " (" + employee.getEmployeeId() + ")");
        }

        // Rule 3: One employee can have a maximum of two active SIM cards
        List<SimAssignment> activeEmployeeSims = simAssignmentRepository.findByEmployeeIdAndStatus(employee.getEmployeeId(), "Active");
        if (activeEmployeeSims.size() >= 2) {
            throw new BusinessRuleException("Employee " + employee.getName() + " already has the maximum of 2 active SIM cards assigned.");
        }

        // Mobile verification
        Mobile mobile = mobileRepository.findByMobileId(request.getMobileId())
                .or(() -> mobileRepository.findById(request.getMobileId()))
                .orElseThrow(() -> new ResourceNotFoundException("Mobile phone not found with ID: " + request.getMobileId()));

        // Mobile Assignment Rule: Do not allow SIM assignments without a valid active mobile assignment
        if (!"Assigned".equalsIgnoreCase(mobile.getStatus()) || mobile.getAssignedEmployeeId() == null) {
            throw new BusinessRuleException("The selected mobile phone (" + mobile.getName() + " - " + mobile.getMobileId() +
                    ") is not currently assigned to any employee. SIM must be associated with an active assigned mobile phone.");
        }

        // Rule: SIMs must belong to the employee who currently holds the mobile phone
        if (!mobile.getAssignedEmployeeId().equals(employee.getEmployeeId())) {
            throw new BusinessRuleException("Mobile phone " + mobile.getName() + " is currently assigned to " +
                    mobile.getAssignedEmployeeName() + " (" + mobile.getAssignedEmployeeId() + "), not " +
                    employee.getName() + " (" + employee.getEmployeeId() + "). A SIM must belong to the employee holding the mobile phone.");
        }

        // Rule 4 & SIM Slot Configuration Specifications:
        List<SimAssignment> activeMobileSims = simAssignmentRepository.findByMobileIdAndStatus(mobile.getMobileId(), "Active");
        String slotConfig = mobile.getSimSlotConfig() != null ? mobile.getSimSlotConfig() : "Dual SIM";
        int maxCapacity = 2;

        if ("Single SIM".equalsIgnoreCase(slotConfig)) {
            maxCapacity = 1;
            if ("eSIM".equalsIgnoreCase(sim.getSimType())) {
                throw new BusinessRuleException("Device Specification Incompatible: Mobile " + mobile.getName() +
                        " has a 'Single SIM' physical slot specification and does not support eSIM profiles. Please assign a Physical SIM.");
            }
        } else if ("Dual SIM".equalsIgnoreCase(slotConfig)) {
            maxCapacity = 2;
            if ("eSIM".equalsIgnoreCase(sim.getSimType())) {
                throw new BusinessRuleException("Device Specification Incompatible: Mobile " + mobile.getName() +
                        " has a 'Dual SIM' physical slot specification and does not support eSIM profiles. Please assign a Physical SIM.");
            }
        } else if ("eSIM".equalsIgnoreCase(slotConfig)) {
            maxCapacity = 1;
            if (!"eSIM".equalsIgnoreCase(sim.getSimType())) {
                throw new BusinessRuleException("Device Specification Incompatible: Mobile " + mobile.getName() +
                        " is an eSIM-only specification. Physical SIM cards cannot be inserted. Please assign an eSIM card.");
            }
        } else if ("Hybrid SIM".equalsIgnoreCase(slotConfig)) {
            maxCapacity = 2;
        }

        if (activeMobileSims.size() >= maxCapacity || mobile.getActiveSimCount() >= maxCapacity) {
            throw new BusinessRuleException("Mobile device specification capacity reached: " + mobile.getName() +
                    " has '" + slotConfig + "' specification which allows a maximum of " + maxCapacity +
                    " active SIM card(s). Current active SIM count is " + mobile.getActiveSimCount() + ".");
        }

        // Determine explicit target slot (Slot 1 vs Slot 2)
        int targetSlot;
        Integer requestedSlot = request.getSlotNumber();
        if (requestedSlot != null && requestedSlot > 0) {
            if (requestedSlot > maxCapacity) {
                throw new BusinessRuleException("Target slot " + requestedSlot + " is not supported on a '" + slotConfig + "' device (Maximum " + maxCapacity + " slot).");
            }
            if (requestedSlot == 1) {
                if (mobile.getSlot1SimId() != null && !mobile.getSlot1SimId().isEmpty()) {
                    throw new BusinessRuleException("Slot 1 is already occupied by " + mobile.getSlot1MobileNumber() +
                            " (" + mobile.getSlot1Provider() + "). Please choose Slot 2 or return the current SIM.");
                }
                targetSlot = 1;
            } else if (requestedSlot == 2) {
                if (mobile.getSlot2SimId() != null && !mobile.getSlot2SimId().isEmpty()) {
                    throw new BusinessRuleException("Slot 2 is already occupied by " + mobile.getSlot2MobileNumber() +
                            " (" + mobile.getSlot2Provider() + "). Please choose Slot 1 or return the current SIM.");
                }
                targetSlot = 2;
            } else {
                throw new BusinessRuleException("Invalid slot number: " + requestedSlot + ". Supported slots are 1 or 2.");
            }
        } else {
            // Auto-detect first empty slot
            if (mobile.getSlot1SimId() == null || mobile.getSlot1SimId().isEmpty()) {
                targetSlot = 1;
            } else if (maxCapacity >= 2 && (mobile.getSlot2SimId() == null || mobile.getSlot2SimId().isEmpty())) {
                targetSlot = 2;
            } else {
                targetSlot = 1;
            }
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
                request.getIssuedDate(),
                request.getRemarks()
        );
        assignment.setSlotNumber(targetSlot);
        SimAssignment savedAssignment = simAssignmentRepository.save(assignment);

        // Update SIM Card
        sim.setStatus("Assigned");
        sim.setAssignedEmployeeId(employee.getEmployeeId());
        sim.setAssignedEmployeeName(employee.getName());
        sim.setAssignedMobileId(mobile.getMobileId());
        sim.setAssignedMobileName(mobile.getName());
        sim.setUpdatedAt(LocalDateTime.now());
        simCardRepository.save(sim);

        // Update Mobile phone slot mapping
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
        mobileRepository.save(mobile);

        return savedAssignment;
    }

    public SimCard returnSim(String simId, SimReturnRequest request) {
        SimCard sim = simCardRepository.findBySimId(simId)
                .or(() -> simCardRepository.findById(simId))
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with ID: " + simId));

        if (!"Assigned".equalsIgnoreCase(sim.getStatus())) {
            throw new BusinessRuleException("Cannot return SIM card. Current status is " + sim.getStatus() + ", not 'Assigned'.");
        }

        // Find active assignment
        Optional<SimAssignment> activeAssignmentOpt = simAssignmentRepository.findBySimIdAndStatus(sim.getSimId(), "Active");
        String associatedMobileId = sim.getAssignedMobileId();
        Integer assignedSlot = null;

        if (activeAssignmentOpt.isPresent()) {
            SimAssignment assignment = activeAssignmentOpt.get();
            assignedSlot = assignment.getSlotNumber();
            assignment.setStatus("Returned");
            assignment.setReturnedDate(request.getReturnedDate());
            if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
                assignment.setRemarks((assignment.getRemarks() != null ? assignment.getRemarks() + " | Return: " : "") + request.getRemarks());
            }
            simAssignmentRepository.save(assignment);
        }

        // Update SIM
        sim.setStatus("Available");
        sim.setAssignedEmployeeId(null);
        sim.setAssignedEmployeeName(null);
        sim.setAssignedMobileId(null);
        sim.setAssignedMobileName(null);
        sim.setUpdatedAt(LocalDateTime.now());
        SimCard savedSim = simCardRepository.save(sim);

        // Update mobile phone slot mapping
        if (associatedMobileId != null) {
            Optional<Mobile> mobOpt = mobileRepository.findByMobileId(associatedMobileId);
            if (mobOpt.isPresent()) {
                Mobile mobile = mobOpt.get();
                if (sim.getSimId().equals(mobile.getSlot1SimId()) || (assignedSlot != null && assignedSlot == 1)) {
                    mobile.setSlot1SimId(null);
                    mobile.setSlot1MobileNumber(null);
                    mobile.setSlot1Provider(null);
                } else if (sim.getSimId().equals(mobile.getSlot2SimId()) || (assignedSlot != null && assignedSlot == 2)) {
                    mobile.setSlot2SimId(null);
                    mobile.setSlot2MobileNumber(null);
                    mobile.setSlot2Provider(null);
                }

                int newCount = 0;
                if (mobile.getSlot1SimId() != null && !mobile.getSlot1SimId().isEmpty()) newCount++;
                if (mobile.getSlot2SimId() != null && !mobile.getSlot2SimId().isEmpty()) newCount++;
                mobile.setActiveSimCount(newCount);
                mobile.setUpdatedAt(LocalDateTime.now());
                mobileRepository.save(mobile);
            }
        }

        return savedSim;
    }
}
