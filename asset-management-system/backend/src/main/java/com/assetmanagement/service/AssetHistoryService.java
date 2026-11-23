package com.assetmanagement.service;

import com.assetmanagement.dto.AssetHistoryDto;
import com.assetmanagement.model.LaptopAssignment;
import com.assetmanagement.model.MobileAssignment;
import com.assetmanagement.model.SimAssignment;
import com.assetmanagement.repository.LaptopAssignmentRepository;
import com.assetmanagement.repository.MobileAssignmentRepository;
import com.assetmanagement.repository.SimAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetHistoryService {

    private final LaptopAssignmentRepository laptopAssignmentRepository;
    private final MobileAssignmentRepository mobileAssignmentRepository;
    private final SimAssignmentRepository simAssignmentRepository;

    public AssetHistoryService(LaptopAssignmentRepository laptopAssignmentRepository,
                               MobileAssignmentRepository mobileAssignmentRepository,
                               SimAssignmentRepository simAssignmentRepository) {
        this.laptopAssignmentRepository = laptopAssignmentRepository;
        this.mobileAssignmentRepository = mobileAssignmentRepository;
        this.simAssignmentRepository = simAssignmentRepository;
    }

    public List<AssetHistoryDto> getAllAssetHistory() {
        List<AssetHistoryDto> allHistory = new ArrayList<>();

        for (LaptopAssignment la : laptopAssignmentRepository.findAll()) {
            allHistory.add(new AssetHistoryDto(
                    la.getId(),
                    la.getEmployeeName(),
                    la.getEmployeeId(),
                    "Laptop",
                    la.getLaptopName() + " (" + la.getLaptopCode() + ")",
                    la.getLaptopSerial(),
                    null,
                    la.getAssignedDate(),
                    la.getReturnedDate(),
                    la.getStatus(),
                    la.getReturnCondition(),
                    la.getRemarks()
            ));
        }

        for (MobileAssignment ma : mobileAssignmentRepository.findAll()) {
            allHistory.add(new AssetHistoryDto(
                    ma.getId(),
                    ma.getEmployeeName(),
                    ma.getEmployeeId(),
                    "Mobile",
                    ma.getMobileName() + " (" + ma.getMobileCode() + ")",
                    ma.getImeiNumber(),
                    null,
                    ma.getAssignedDate(),
                    ma.getReturnedDate(),
                    ma.getStatus(),
                    ma.getReturnCondition(),
                    ma.getRemarks()
            ));
        }

        for (SimAssignment sa : simAssignmentRepository.findAll()) {
            allHistory.add(new AssetHistoryDto(
                    sa.getId(),
                    sa.getEmployeeName(),
                    sa.getEmployeeId(),
                    "SIM",
                    sa.getProvider() + " SIM (" + sa.getSimCode() + ")",
                    sa.getMobileNumber(),
                    sa.getProvider(),
                    sa.getIssuedDate(),
                    sa.getReturnedDate(),
                    sa.getStatus(),
                    null,
                    sa.getRemarks()
            ));
        }

        // Sort by assignedDate descending
        allHistory.sort(Comparator.comparing(AssetHistoryDto::getAssignedDate, Comparator.nullsLast(Comparator.reverseOrder())));
        return allHistory;
    }

    public List<AssetHistoryDto> getEmployeeWiseHistory(String employeeId) {
        return getAllAssetHistory().stream()
                .filter(h -> h.getEmployeeId() != null && h.getEmployeeId().equalsIgnoreCase(employeeId))
                .collect(Collectors.toList());
    }

    public List<AssetHistoryDto> getAssetWiseHistory(String assetType, String identifier) {
        return getAllAssetHistory().stream()
                .filter(h -> {
                    boolean matchesType = (assetType == null || assetType.equalsIgnoreCase(h.getAssetType()));
                    boolean matchesIdent = (identifier == null || identifier.equalsIgnoreCase(h.getAssetIdentifier()));
                    return matchesType && matchesIdent;
                })
                .collect(Collectors.toList());
    }

    public List<AssetHistoryDto> searchAndFilter(String employeeQuery, String assetType, String status, LocalDate fromDate, LocalDate toDate) {
        return getAllAssetHistory().stream()
                .filter(h -> {
                    boolean matchesEmp = true;
                    if (employeeQuery != null && !employeeQuery.trim().isEmpty()) {
                        String q = employeeQuery.toLowerCase().trim();
                        matchesEmp = (h.getEmployeeName() != null && h.getEmployeeName().toLowerCase().contains(q)) ||
                                     (h.getEmployeeId() != null && h.getEmployeeId().toLowerCase().contains(q)) ||
                                     (h.getAssetIdentifier() != null && h.getAssetIdentifier().toLowerCase().contains(q));
                    }
                    boolean matchesType = (assetType == null || assetType.trim().isEmpty() || "ALL".equalsIgnoreCase(assetType))
                            || (h.getAssetType() != null && h.getAssetType().equalsIgnoreCase(assetType.trim()));
                    boolean matchesStatus = (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status))
                            || (h.getAssignmentStatus() != null && h.getAssignmentStatus().equalsIgnoreCase(status.trim()));
                    boolean matchesFrom = (fromDate == null) || (h.getAssignedDate() != null && !h.getAssignedDate().isBefore(fromDate));
                    boolean matchesTo = (toDate == null) || (h.getAssignedDate() != null && !h.getAssignedDate().isAfter(toDate));

                    return matchesEmp && matchesType && matchesStatus && matchesFrom && matchesTo;
                })
                .collect(Collectors.toList());
    }
}
