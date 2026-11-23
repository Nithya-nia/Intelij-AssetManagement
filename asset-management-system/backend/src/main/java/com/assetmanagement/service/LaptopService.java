package com.assetmanagement.service;

import com.assetmanagement.dto.LaptopAssignRequest;
import com.assetmanagement.dto.LaptopReturnRequest;
import com.assetmanagement.exception.BusinessRuleException;
import com.assetmanagement.exception.DuplicateResourceException;
import com.assetmanagement.exception.ResourceNotFoundException;
import com.assetmanagement.model.Employee;
import com.assetmanagement.model.Laptop;
import com.assetmanagement.model.LaptopAssignment;
import com.assetmanagement.repository.EmployeeRepository;
import com.assetmanagement.repository.LaptopAssignmentRepository;
import com.assetmanagement.repository.LaptopRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LaptopService {

    private final LaptopRepository laptopRepository;
    private final LaptopAssignmentRepository laptopAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public LaptopService(LaptopRepository laptopRepository,
                         LaptopAssignmentRepository laptopAssignmentRepository,
                         EmployeeRepository employeeRepository,
                         SequenceGeneratorService sequenceGeneratorService) {
        this.laptopRepository = laptopRepository;
        this.laptopAssignmentRepository = laptopAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAll();
    }

    public Laptop getLaptopById(String id) {
        return laptopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laptop not found with id: " + id));
    }

    public Laptop getLaptopByLaptopId(String laptopId) {
        return laptopRepository.findByLaptopId(laptopId)
                .orElseThrow(() -> new ResourceNotFoundException("Laptop not found with laptopId: " + laptopId));
    }

    public String peekNextLaptopId() {
        return sequenceGeneratorService.peekNextLaptopId();
    }

    public Laptop createLaptop(Laptop laptop) {
        if (laptop.getProjectName() == null || laptop.getProjectName().trim().isEmpty()) {
            throw new BusinessRuleException("Project Name is required when registering a laptop asset.");
        }

        // Auto-generate Laptop ID if null, empty, or placeholder
        if (laptop.getLaptopId() == null || laptop.getLaptopId().trim().isEmpty() || "AUTO".equalsIgnoreCase(laptop.getLaptopId().trim())) {
            laptop.setLaptopId(sequenceGeneratorService.generateLaptopId());
        } else {
            // Verify and auto-increment sequence if supplied
            if (laptopRepository.existsByLaptopId(laptop.getLaptopId())) {
                // If it already exists, generate a fresh unique sequence ID
                laptop.setLaptopId(sequenceGeneratorService.generateLaptopId());
            }
        }

        if (laptopRepository.existsBySerialNumber(laptop.getSerialNumber())) {
            throw new DuplicateResourceException("Laptop with Serial Number " + laptop.getSerialNumber() + " already exists");
        }
        laptop.setCreatedAt(LocalDateTime.now());
        laptop.setUpdatedAt(LocalDateTime.now());
        return laptopRepository.save(laptop);
    }

    public Laptop updateLaptop(String id, Laptop updatedData) {
        Laptop existing = getLaptopById(id);

        if (updatedData.getProjectName() != null && !updatedData.getProjectName().trim().isEmpty()) {
            existing.setProjectName(updatedData.getProjectName().trim());
        }

        if (!existing.getLaptopId().equals(updatedData.getLaptopId()) &&
                laptopRepository.existsByLaptopId(updatedData.getLaptopId())) {
            throw new DuplicateResourceException("Laptop with ID " + updatedData.getLaptopId() + " already exists");
        }
        if (!existing.getSerialNumber().equals(updatedData.getSerialNumber()) &&
                laptopRepository.existsBySerialNumber(updatedData.getSerialNumber())) {
            throw new DuplicateResourceException("Laptop with Serial Number " + updatedData.getSerialNumber() + " already exists");
        }

        existing.setLaptopId(updatedData.getLaptopId());
        existing.setName(updatedData.getName());
        existing.setBrand(updatedData.getBrand());
        existing.setSerialNumber(updatedData.getSerialNumber());
        existing.setConfiguration(updatedData.getConfiguration());
        existing.setProcessor(updatedData.getProcessor());
        existing.setRam(updatedData.getRam());
        existing.setStorage(updatedData.getStorage());
        existing.setPurchaseDate(updatedData.getPurchaseDate());
        existing.setWarrantyExpiryDate(updatedData.getWarrantyExpiryDate());
        
        // Only update status directly if not currently assigned
        if (!"Assigned".equalsIgnoreCase(existing.getStatus()) && !"Assigned".equalsIgnoreCase(updatedData.getStatus())) {
            existing.setStatus(updatedData.getStatus());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return laptopRepository.save(existing);
    }

    public Laptop assignLaptop(String id, LaptopAssignRequest request) {
        Laptop laptop = getLaptopById(id);

        // Rule 5: One laptop can be assigned to only one employee at a time. Must be Available
        if (!"Available".equalsIgnoreCase(laptop.getStatus())) {
            throw new BusinessRuleException("Laptop cannot be assigned. Current status is " + laptop.getStatus() +
                    ". Only 'Available' laptops can be assigned.");
        }

        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        // Rule 11: Inactive employees cannot receive new assets
        if ("Inactive".equalsIgnoreCase(employee.getStatus())) {
            throw new BusinessRuleException("Cannot assign laptop to inactive employee: " + employee.getName() + " (" + employee.getEmployeeId() + ")");
        }

        // Rule 1: One employee can have a maximum of one active laptop
        Optional<Laptop> existingEmployeeLaptop = laptopRepository.findByAssignedEmployeeId(employee.getEmployeeId());
        if (existingEmployeeLaptop.isPresent()) {
            throw new BusinessRuleException("Employee " + employee.getName() + " already has an active laptop assigned (" +
                    existingEmployeeLaptop.get().getLaptopId() + " - " + existingEmployeeLaptop.get().getName() + "). Max 1 laptop allowed.");
        }

        // Create assignment record
        LaptopAssignment assignment = new LaptopAssignment(
                laptop.getId(),
                laptop.getLaptopId(),
                laptop.getName(),
                laptop.getSerialNumber(),
                employee.getEmployeeId(),
                employee.getName(),
                employee.getDepartment(),
                request.getAssignedDate(),
                request.getRemarks()
        );
        laptopAssignmentRepository.save(assignment);

        // Update laptop
        laptop.setStatus("Assigned");
        laptop.setAssignedEmployeeId(employee.getEmployeeId());
        laptop.setAssignedEmployeeName(employee.getName());
        laptop.setAssignedDate(request.getAssignedDate());
        laptop.setUpdatedAt(LocalDateTime.now());

        return laptopRepository.save(laptop);
    }

    public Laptop returnLaptop(String id, LaptopReturnRequest request) {
        Laptop laptop = getLaptopById(id);

        if (!"Assigned".equalsIgnoreCase(laptop.getStatus())) {
            throw new BusinessRuleException("Cannot return laptop. Current status is " + laptop.getStatus() + ", not 'Assigned'.");
        }

        // Find active assignment
        Optional<LaptopAssignment> activeAssignment = laptopAssignmentRepository.findByLaptopIdAndStatus(laptop.getId(), "Active");
        if (activeAssignment.isPresent()) {
            LaptopAssignment assignment = activeAssignment.get();
            assignment.setStatus("Returned");
            assignment.setReturnedDate(request.getReturnedDate());
            assignment.setReturnCondition(request.getReturnCondition());
            if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
                assignment.setRemarks((assignment.getRemarks() != null ? assignment.getRemarks() + " | Return: " : "") + request.getRemarks());
            }
            laptopAssignmentRepository.save(assignment);
        }

        // Update laptop
        laptop.setStatus("Available");
        laptop.setAssignedEmployeeId(null);
        laptop.setAssignedEmployeeName(null);
        laptop.setAssignedDate(null);
        laptop.setUpdatedAt(LocalDateTime.now());

        return laptopRepository.save(laptop);
    }

    public List<LaptopAssignment> getLaptopHistory(String id) {
        Laptop laptop = getLaptopById(id);
        return laptopAssignmentRepository.findByLaptopIdOrderByCreatedAtDesc(laptop.getId());
    }

    public List<Laptop> searchAndFilter(String query, String brand, String status, String project) {
        return laptopRepository.findAll().stream()
                .filter(l -> {
                    boolean matchesQuery = true;
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.toLowerCase().trim();
                        matchesQuery = (l.getName() != null && l.getName().toLowerCase().contains(q)) ||
                                       (l.getLaptopId() != null && l.getLaptopId().toLowerCase().contains(q)) ||
                                       (l.getSerialNumber() != null && l.getSerialNumber().toLowerCase().contains(q)) ||
                                       (l.getProjectName() != null && l.getProjectName().toLowerCase().contains(q)) ||
                                       (l.getAssignedEmployeeName() != null && l.getAssignedEmployeeName().toLowerCase().contains(q));
                    }
                    boolean matchesBrand = (brand == null || brand.trim().isEmpty() || "ALL".equalsIgnoreCase(brand))
                            || (l.getBrand() != null && l.getBrand().equalsIgnoreCase(brand.trim()));
                    boolean matchesStatus = (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status))
                            || (l.getStatus() != null && l.getStatus().equalsIgnoreCase(status.trim()));
                    boolean matchesProject = (project == null || project.trim().isEmpty() || "ALL".equalsIgnoreCase(project))
                            || (l.getProjectName() != null && l.getProjectName().equalsIgnoreCase(project.trim()));

                    return matchesQuery && matchesBrand && matchesStatus && matchesProject;
                })
                .collect(Collectors.toList());
    }

    public void deleteLaptop(String id) {
        Laptop laptop = getLaptopById(id);

        if ("Assigned".equalsIgnoreCase(laptop.getStatus()) || laptop.getAssignedEmployeeId() != null) {
            throw new com.assetmanagement.exception.BusinessRuleException(
                    "Cannot delete laptop " + laptop.getLaptopId() + " (" + laptop.getName() +
                    ") because it is currently assigned to " + laptop.getAssignedEmployeeName() +
                    ". Please return the laptop before deleting."
            );
        }

        laptopRepository.delete(laptop);
    }
}
