package com.assetmanagement.controller;

import com.assetmanagement.dto.RechargeRequest;
import com.assetmanagement.model.Recharge;
import com.assetmanagement.service.RechargeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recharges")
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @GetMapping
    public ResponseEntity<List<Recharge>> getRecharges(
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String employee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        if (mobileNumber != null || provider != null || employee != null || fromDate != null || toDate != null) {
            return ResponseEntity.ok(rechargeService.searchAndFilter(mobileNumber, provider, employee, fromDate, toDate));
        }
        return ResponseEntity.ok(rechargeService.getAllRecharges());
    }

    @PostMapping
    public ResponseEntity<Recharge> addRecharge(@Valid @RequestBody RechargeRequest request) {
        Recharge created = rechargeService.addRecharge(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/sim/{simId}")
    public ResponseEntity<List<Recharge>> getRechargesBySimId(@PathVariable String simId) {
        return ResponseEntity.ok(rechargeService.getRechargesBySimId(simId));
    }
}
