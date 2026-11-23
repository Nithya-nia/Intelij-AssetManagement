package com.assetmanagement.service;

import com.assetmanagement.dto.AssetHistoryDto;
import com.assetmanagement.dto.EmployeeAssetsDto;
import com.assetmanagement.exception.DuplicateResourceException;
import com.assetmanagement.exception.ResourceNotFoundException;
import com.assetmanagement.model.Employee;
import com.assetmanagement.model.Laptop;
import com.assetmanagement.model.Mobile;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.repository.EmployeeRepository;
import com.assetmanagement.repository.LaptopRepository;
import com.assetmanagement.repository.MobileRepository;
import com.assetmanagement.repository.SimCardRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final LaptopRepository laptopRepository;
    private final MobileRepository mobileRepository;
    private final SimCardRepository simCardRepository;
    private final AssetHistoryService assetHistoryService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           LaptopRepository laptopRepository,
                           MobileRepository mobileRepository,
                           SimCardRepository simCardRepository,
                           @Lazy AssetHistoryService assetHistoryService) {
        this.employeeRepository = employeeRepository;
        this.laptopRepository = laptopRepository;
        this.mobileRepository = mobileRepository;
        this.simCardRepository = simCardRepository;
        this.assetHistoryService = assetHistoryService;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));
    }

    private static final java.util.Set<String> ALLOWED_DEPARTMENTS = java.util.Set.of("HR", "Developers", "Admin");

    private void validateDepartment(String department) {
        if (department == null || !ALLOWED_DEPARTMENTS.contains(department.trim())) {
            throw new com.assetmanagement.exception.BusinessRuleException("Invalid department. Department must be one of: HR, Developers, Admin");
        }
    }

    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new DuplicateResourceException("Employee with ID " + employee.getEmployeeId() + " already exists");
        }
        validateDepartment(employee.getDepartment());
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(String id, Employee updatedData) {
        Employee existing = getEmployeeById(id);

        if (!existing.getEmployeeId().equals(updatedData.getEmployeeId()) &&
                employeeRepository.existsByEmployeeId(updatedData.getEmployeeId())) {
            throw new DuplicateResourceException("Employee with ID " + updatedData.getEmployeeId() + " already exists");
        }
        validateDepartment(updatedData.getDepartment());

        existing.setEmployeeId(updatedData.getEmployeeId());
        existing.setName(updatedData.getName());
        existing.setDepartment(updatedData.getDepartment());
        existing.setDesignation(updatedData.getDesignation());
        existing.setJoiningDate(updatedData.getJoiningDate());
        existing.setEmail(updatedData.getEmail());
        existing.setPhone(updatedData.getPhone());
        existing.setStatus(updatedData.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        return employeeRepository.save(existing);
    }

    public List<Employee> searchAndFilter(String query, String department, String status) {
        return employeeRepository.findAll().stream()
                .filter(e -> {
                    boolean matchesQuery = true;
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.toLowerCase().trim();
                        matchesQuery = (e.getName() != null && e.getName().toLowerCase().contains(q)) ||
                                       (e.getEmployeeId() != null && e.getEmployeeId().toLowerCase().contains(q)) ||
                                       (e.getDesignation() != null && e.getDesignation().toLowerCase().contains(q)) ||
                                       (e.getEmail() != null && e.getEmail().toLowerCase().contains(q));
                    }
                    boolean matchesDept = (department == null || department.trim().isEmpty() || "ALL".equalsIgnoreCase(department))
                            || (e.getDepartment() != null && e.getDepartment().equalsIgnoreCase(department.trim()));
                    boolean matchesStatus = (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status))
                            || (e.getStatus() != null && e.getStatus().equalsIgnoreCase(status.trim()));

                    return matchesQuery && matchesDept && matchesStatus;
                })
                .collect(Collectors.toList());
    }

    public EmployeeAssetsDto getEmployeeAssets(String employeeId) {
        Employee employee = getEmployeeByEmployeeId(employeeId);

        Laptop laptop = laptopRepository.findByAssignedEmployeeId(employeeId).orElse(null);
        Mobile mobile = mobileRepository.findByAssignedEmployeeId(employeeId).orElse(null);
        List<SimCard> sims = simCardRepository.findByAssignedEmployeeId(employeeId);
        List<AssetHistoryDto> history = assetHistoryService.getEmployeeWiseHistory(employeeId);

        return new EmployeeAssetsDto(employee.getEmployeeId(), employee.getName(), laptop, mobile, sims, history);
    }

    public void deleteEmployee(String id) {
        Employee employee = getEmployeeById(id);

        // Safety check 1: Active Laptop
        Optional<Laptop> activeLaptop = laptopRepository.findByAssignedEmployeeId(employee.getEmployeeId());
        if (activeLaptop.isPresent()) {
            throw new com.assetmanagement.exception.BusinessRuleException(
                    "Cannot delete employee " + employee.getName() + " (" + employee.getEmployeeId() +
                    ") because they currently hold assigned laptop: " + activeLaptop.get().getLaptopId() +
                    " (" + activeLaptop.get().getName() + "). Please return the laptop before deleting the employee."
            );
        }

        // Safety check 2: Active Mobile
        Optional<Mobile> activeMobile = mobileRepository.findByAssignedEmployeeId(employee.getEmployeeId());
        if (activeMobile.isPresent()) {
            throw new com.assetmanagement.exception.BusinessRuleException(
                    "Cannot delete employee " + employee.getName() + " (" + employee.getEmployeeId() +
                    ") because they currently hold assigned mobile: " + activeMobile.get().getMobileId() +
                    " (" + activeMobile.get().getName() + "). Please return the mobile before deleting the employee."
            );
        }

        // Safety check 3: Active SIM cards
        List<SimCard> activeSims = simCardRepository.findByAssignedEmployeeId(employee.getEmployeeId());
        if (!activeSims.isEmpty()) {
            throw new com.assetmanagement.exception.BusinessRuleException(
                    "Cannot delete employee " + employee.getName() + " (" + employee.getEmployeeId() +
                    ") because they currently hold " + activeSims.size() +
                    " active SIM card(s). Please return the SIM cards before deleting the employee."
            );
        }

        employeeRepository.delete(employee);
    }
}
