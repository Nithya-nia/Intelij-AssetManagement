package com.assetmanagement.controller;

import com.assetmanagement.dto.SimMappingRequest;
import com.assetmanagement.dto.SimUnmapRequest;
import com.assetmanagement.model.Mobile;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.service.SimMappingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sim-mapping")
public class SimMappingController {

    private final SimMappingService simMappingService;

    public SimMappingController(SimMappingService simMappingService) {
        this.simMappingService = simMappingService;
    }

    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getSimMappingStatuses() {
        return ResponseEntity.ok(simMappingService.getSimMappingStatuses());
    }

    @GetMapping("/available-sims")
    public ResponseEntity<List<SimCard>> getAvailableSimsForMapping() {
        return ResponseEntity.ok(simMappingService.getAvailableSimsForMapping());
    }

    @PostMapping("/map")
    public ResponseEntity<Mobile> mapSimToMobile(@Valid @RequestBody SimMappingRequest request) {
        return ResponseEntity.ok(simMappingService.mapSimToMobile(request));
    }

    @PostMapping("/unmap")
    public ResponseEntity<Mobile> unmapSimFromMobile(@Valid @RequestBody SimUnmapRequest request) {
        return ResponseEntity.ok(simMappingService.unmapSimFromMobile(request));
    }
}
