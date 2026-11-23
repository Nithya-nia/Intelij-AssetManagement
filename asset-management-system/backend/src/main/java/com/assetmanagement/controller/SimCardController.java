package com.assetmanagement.controller;

import com.assetmanagement.model.Recharge;
import com.assetmanagement.model.SimAssignment;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.service.SimCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sims")
public class SimCardController {

    private final SimCardService simCardService;

    public SimCardController(SimCardService simCardService) {
        this.simCardService = simCardService;
    }

    @GetMapping
    public ResponseEntity<List<SimCard>> getSims(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String status) {
        if (search != null || provider != null || status != null) {
            return ResponseEntity.ok(simCardService.searchAndFilter(search, provider, status));
        }
        return ResponseEntity.ok(simCardService.getAllSims());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimCard> getSimById(@PathVariable String id) {
        return ResponseEntity.ok(simCardService.getSimById(id));
    }

    @PostMapping
    public ResponseEntity<SimCard> createSim(@Valid @RequestBody SimCard sim) {
        SimCard created = simCardService.createSim(sim);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimCard> updateSim(@PathVariable String id, @Valid @RequestBody SimCard sim) {
        SimCard updated = simCardService.updateSim(id, sim);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<SimAssignment>> getSimHistory(@PathVariable String id) {
        return ResponseEntity.ok(simCardService.getSimHistory(id));
    }

    @GetMapping("/{id}/recharges")
    public ResponseEntity<List<Recharge>> getSimRecharges(@PathVariable String id) {
        return ResponseEntity.ok(simCardService.getSimRecharges(id));
    }
}
