package com.assetmanagement.controller;

import com.assetmanagement.dto.AssetHistoryDto;
import com.assetmanagement.service.AssetHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asset-history")
public class AssetHistoryController {

    private final AssetHistoryService assetHistoryService;

    public AssetHistoryController(AssetHistoryService assetHistoryService) {
        this.assetHistoryService = assetHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<AssetHistoryDto>> getAssetHistory(
            @RequestParam(required = false) String employee,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        if (employee != null || assetType != null || status != null || fromDate != null || toDate != null) {
            return ResponseEntity.ok(assetHistoryService.searchAndFilter(employee, assetType, status, fromDate, toDate));
        }
        return ResponseEntity.ok(assetHistoryService.getAllAssetHistory());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AssetHistoryDto>> getEmployeeHistory(@PathVariable String employeeId) {
        return ResponseEntity.ok(assetHistoryService.getEmployeeWiseHistory(employeeId));
    }

    @GetMapping("/asset")
    public ResponseEntity<List<AssetHistoryDto>> getAssetWiseHistory(
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String identifier) {
        return ResponseEntity.ok(assetHistoryService.getAssetWiseHistory(assetType, identifier));
    }
}
