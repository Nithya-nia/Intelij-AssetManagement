package com.assetmanagement.service;

import com.assetmanagement.dto.MobileAssignRequest;
import com.assetmanagement.dto.MobileReturnRequest;
import com.assetmanagement.exception.BusinessRuleException;
import com.assetmanagement.exception.DuplicateResourceException;
import com.assetmanagement.exception.ResourceNotFoundException;
import com.assetmanagement.model.Employee;
import com.assetmanagement.model.Mobile;
import com.assetmanagement.model.MobileAssignment;
import com.assetmanagement.model.SimAssignment;
import com.assetmanagement.repository.EmployeeRepository;
import com.assetmanagement.repository.MobileAssignmentRepository;
import com.assetmanagement.repository.MobileRepository;
import com.assetmanagement.repository.SimAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MobileService {

    private final MobileRepository mobileRepository;
    private final MobileAssignmentRepository mobileAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SimAssignmentRepository simAssignmentRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public MobileService(MobileRepository mobileRepository,
                         MobileAssignmentRepository mobileAssignmentRepository,
                         EmployeeRepository employeeRepository,
                         SimAssignmentRepository simAssignmentRepository,
                         SequenceGeneratorService sequenceGeneratorService) {
        this.mobileRepository = mobileRepository;
        this.mobileAssignmentRepository = mobileAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.simAssignmentRepository = simAssignmentRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public String peekNextMobileId() {
        return sequenceGeneratorService.peekNextMobileId();
    }

    public List<Mobile> getAllMobiles() {
        return mobileRepository.findAll();
    }

    public Mobile getMobileById(String id) {
        return mobileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mobile phone not found with id: " + id));
    }

    public Mobile getMobileByMobileId(String mobileId) {
        return mobileRepository.findByMobileId(mobileId)
                .orElseThrow(() -> new ResourceNotFoundException("Mobile phone not found with mobileId: " + mobileId));
    }

    public Mobile createMobile(Mobile mobile) {
        if (mobile.getProjectName() == null || mobile.getProjectName().trim().isEmpty()) {
            throw new BusinessRuleException("Project Name is required when registering a mobile device.");
        }

        // Auto-generate Mobile ID if null, empty, or placeholder
        if (mobile.getMobileId() == null || mobile.getMobileId().trim().isEmpty() || "AUTO".equalsIgnoreCase(mobile.getMobileId().trim())) {
            mobile.setMobileId(sequenceGeneratorService.generateMobileId());
        } else {
            if (mobileRepository.existsByMobileId(mobile.getMobileId())) {
                mobile.setMobileId(sequenceGeneratorService.generateMobileId());
            }
        }

        if (mobileRepository.existsByImeiNumber(mobile.getImeiNumber())) {
            throw new DuplicateResourceException("Mobile with IMEI " + mobile.getImeiNumber() + " already exists");
        }

        // SIM slot config: only Single SIM or Dual SIM
        String slotConfig = mobile.getSimSlotConfig();
        if (slotConfig == null || (!slotConfig.equalsIgnoreCase("Single SIM") && !slotConfig.equalsIgnoreCase("Dual SIM"))) {
            mobile.setSimSlotConfig("Dual SIM");
        }

        mobile.setActiveSimCount(0);
        mobile.setCreatedAt(LocalDateTime.now());
        mobile.setUpdatedAt(LocalDateTime.now());
        return mobileRepository.save(mobile);
    }

    public Mobile updateMobile(String id, Mobile updatedData) {
        Mobile existing = getMobileById(id);

        if (updatedData.getProjectName() != null && !updatedData.getProjectName().trim().isEmpty()) {
            existing.setProjectName(updatedData.getProjectName().trim());
        }
        if (updatedData.getSimSlotConfig() != null && !updatedData.getSimSlotConfig().trim().isEmpty()) {
            int maxAllowed = "Single SIM".equalsIgnoreCase(updatedData.getSimSlotConfig()) ? 1 : 2;
            if (existing.getActiveSimCount() > maxAllowed) {
                throw new BusinessRuleException("Cannot change slot configuration to " + updatedData.getSimSlotConfig() +
                        " because the device currently has " + existing.getActiveSimCount() + " active SIM cards assigned.");
            }
            existing.setSimSlotConfig(updatedData.getSimSlotConfig());
        }

        if (!existing.getMobileId().equals(updatedData.getMobileId()) &&
                mobileRepository.existsByMobileId(updatedData.getMobileId())) {
            throw new DuplicateResourceException("Mobile with ID " + updatedData.getMobileId() + " already exists");
        }
        if (!existing.getImeiNumber().equals(updatedData.getImeiNumber()) &&
                mobileRepository.existsByImeiNumber(updatedData.getImeiNumber())) {
            throw new DuplicateResourceException("Mobile with IMEI " + updatedData.getImeiNumber() + " already exists");
        }

        existing.setMobileId(updatedData.getMobileId());
        existing.setName(updatedData.getName());
        existing.setBrand(updatedData.getBrand());
        existing.setImeiNumber(updatedData.getImeiNumber());
        existing.setPurchaseDate(updatedData.getPurchaseDate());
        existing.setWarrantyExpiryDate(updatedData.getWarrantyExpiryDate());

        if (!"Assigned".equalsIgnoreCase(existing.getStatus()) && !"Assigned".equalsIgnoreCase(updatedData.getStatus())) {
            existing.setStatus(updatedData.getStatus());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return mobileRepository.save(existing);
    }

    public Mobile assignMobile(String id, MobileAssignRequest request) {
        Mobile mobile = getMobileById(id);

        // Rule 6: One mobile phone can be assigned to only one employee at a time. Must be Available
        if (!"Available".equalsIgnoreCase(mobile.getStatus())) {
            throw new BusinessRuleException("Mobile phone cannot be assigned. Current status is " + mobile.getStatus() +
                    ". Only 'Available' mobiles can be assigned.");
        }

        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        // Rule 11: Inactive employees cannot receive new assets
        if ("Inactive".equalsIgnoreCase(employee.getStatus())) {
            throw new BusinessRuleException("Cannot assign mobile to inactive employee: " + employee.getName() + " (" + employee.getEmployeeId() + ")");
        }

        // Rule 2: One employee can have a maximum of one active mobile phone
        Optional<Mobile> existingEmployeeMobile = mobileRepository.findByAssignedEmployeeId(employee.getEmployeeId());
        if (existingEmployeeMobile.isPresent()) {
            throw new BusinessRuleException("Employee " + employee.getName() + " already has an active mobile assigned (" +
                    existingEmployeeMobile.get().getMobileId() + " - " + existingEmployeeMobile.get().getName() + "). Max 1 mobile phone allowed.");
        }

        // Create assignment record
        MobileAssignment assignment = new MobileAssignment(
                mobile.getId(),
                mobile.getMobileId(),
                mobile.getName(),
                mobile.getImeiNumber(),
                employee.getEmployeeId(),
                employee.getName(),
                employee.getDepartment(),
                request.getAssignedDate(),
                request.getRemarks()
        );
        mobileAssignmentRepository.save(assignment);

        // Update mobile
        mobile.setStatus("Assigned");
        mobile.setAssignedEmployeeId(employee.getEmployeeId());
        mobile.setAssignedEmployeeName(employee.getName());
        mobile.setAssignedDate(request.getAssignedDate());
        mobile.setUpdatedAt(LocalDateTime.now());

        return mobileRepository.save(mobile);
    }

    public Mobile returnMobile(String id, MobileReturnRequest request) {
        Mobile mobile = getMobileById(id);

        if (!"Assigned".equalsIgnoreCase(mobile.getStatus())) {
            throw new BusinessRuleException("Cannot return mobile phone. Current status is " + mobile.getStatus() + ", not 'Assigned'.");
        }

        // CRITICAL BUSINESS RULE: Mobile Return Handling
        // Check active SIM assignments on this phone
        List<SimAssignment> activeSims = simAssignmentRepository.findByMobileIdAndStatus(mobile.getMobileId(), "Active");
        if (!activeSims.isEmpty() || mobile.getActiveSimCount() > 0) {
            long activeCount = !activeSims.isEmpty() ? activeSims.size() : mobile.getActiveSimCount();
            throw new BusinessRuleException("Cannot return mobile phone while active SIM assignments remain. There are currently " +
                    activeCount + " active SIM card(s) associated with this phone (" + mobile.getName() + "). " +
                    "Please return or reassign the SIM cards first to prevent orphan SIM assignments.");
        }

        // Close active assignment
        Optional<MobileAssignment> activeAssignment = mobileAssignmentRepository.findByMobileIdAndStatus(mobile.getId(), "Active");
        if (activeAssignment.isPresent()) {
            MobileAssignment assignment = activeAssignment.get();
            assignment.setStatus("Returned");
            assignment.setReturnedDate(request.getReturnedDate());
            assignment.setReturnCondition(request.getReturnCondition());
            if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
                assignment.setRemarks((assignment.getRemarks() != null ? assignment.getRemarks() + " | Return: " : "") + request.getRemarks());
            }
            mobileAssignmentRepository.save(assignment);
        }

        // Update mobile
        mobile.setStatus("Available");
        mobile.setAssignedEmployeeId(null);
        mobile.setAssignedEmployeeName(null);
        mobile.setAssignedDate(null);
        mobile.setActiveSimCount(0);
        mobile.setUpdatedAt(LocalDateTime.now());

        return mobileRepository.save(mobile);
    }

    public List<MobileAssignment> getMobileHistory(String id) {
        Mobile mobile = getMobileById(id);
        return mobileAssignmentRepository.findByMobileIdOrderByCreatedAtDesc(mobile.getId());
    }

    public List<Mobile> searchAndFilter(String query, String brand, String status, String project, String slotConfig) {
        return mobileRepository.findAll().stream()
                .filter(m -> {
                    boolean matchesQuery = true;
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.toLowerCase().trim();
                        matchesQuery = (m.getName() != null && m.getName().toLowerCase().contains(q)) ||
                                       (m.getMobileId() != null && m.getMobileId().toLowerCase().contains(q)) ||
                                       (m.getImeiNumber() != null && m.getImeiNumber().toLowerCase().contains(q)) ||
                                       (m.getSlot1MobileNumber() != null && m.getSlot1MobileNumber().toLowerCase().contains(q)) ||
                                       (m.getSlot2MobileNumber() != null && m.getSlot2MobileNumber().toLowerCase().contains(q)) ||
                                       (m.getProjectName() != null && m.getProjectName().toLowerCase().contains(q)) ||
                                       (m.getAssignedEmployeeName() != null && m.getAssignedEmployeeName().toLowerCase().contains(q));
                    }
                    boolean matchesBrand = (brand == null || brand.trim().isEmpty() || "ALL".equalsIgnoreCase(brand))
                            || (m.getBrand() != null && m.getBrand().equalsIgnoreCase(brand.trim()));
                    boolean matchesStatus = (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status))
                            || (m.getStatus() != null && m.getStatus().equalsIgnoreCase(status.trim()));
                    boolean matchesProject = (project == null || project.trim().isEmpty() || "ALL".equalsIgnoreCase(project))
                            || (m.getProjectName() != null && m.getProjectName().equalsIgnoreCase(project.trim()));
                    boolean matchesSlot = (slotConfig == null || slotConfig.trim().isEmpty() || "ALL".equalsIgnoreCase(slotConfig))
                            || (m.getSimSlotConfig() != null && m.getSimSlotConfig().equalsIgnoreCase(slotConfig.trim()));

                    return matchesQuery && matchesBrand && matchesStatus && matchesProject && matchesSlot;
                })
                .collect(Collectors.toList());
    }
}
