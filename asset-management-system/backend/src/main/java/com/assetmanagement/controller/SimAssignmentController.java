package com.assetmanagement.controller;

import com.assetmanagement.dto.SimAssignRequest;
import com.assetmanagement.dto.SimReturnRequest;
import com.assetmanagement.model.SimAssignment;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.service.SimAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sim-assignments")
public class SimAssignmentController {

    private final SimAssignmentService simAssignmentService;

    public SimAssignmentController(SimAssignmentService simAssignmentService) {
        this.simAssignmentService = simAssignmentService;
    }

    @GetMapping
    public ResponseEntity<List<SimAssignment>> getAllAssignments() {
        return ResponseEntity.ok(simAssignmentService.getAllAssignments());
    }

    @PostMapping("/{simId}/assign")
    public ResponseEntity<SimAssignment> assignSim(@PathVariable String simId, @Valid @RequestBody SimAssignRequest request) {
        SimAssignment assigned = simAssignmentService.assignSim(simId, request);
        return ResponseEntity.ok(assigned);
    }

    @PostMapping("/{simId}/return")
    public ResponseEntity<SimCard> returnSim(@PathVariable String simId, @Valid @RequestBody SimReturnRequest request) {
        SimCard returned = simAssignmentService.returnSim(simId, request);
        return ResponseEntity.ok(returned);
    }
}
