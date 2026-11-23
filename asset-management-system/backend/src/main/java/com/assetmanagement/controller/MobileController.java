package com.assetmanagement.controller;

import com.assetmanagement.dto.MobileAssignRequest;
import com.assetmanagement.dto.MobileReturnRequest;
import com.assetmanagement.model.Mobile;
import com.assetmanagement.model.MobileAssignment;
import com.assetmanagement.service.MobileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobiles")
public class MobileController {

    private final MobileService mobileService;

    public MobileController(MobileService mobileService) {
        this.mobileService = mobileService;
    }

    @GetMapping
    public ResponseEntity<List<Mobile>> getMobiles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String slotConfig) {
        if (search != null || brand != null || status != null || project != null || slotConfig != null) {
            return ResponseEntity.ok(mobileService.searchAndFilter(search, brand, status, project, slotConfig));
        }
        return ResponseEntity.ok(mobileService.getAllMobiles());
    }

    @GetMapping("/next-id")
    public ResponseEntity<java.util.Map<String, String>> getNextMobileId() {
        return ResponseEntity.ok(java.util.Map.of("nextId", mobileService.peekNextMobileId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mobile> getMobileById(@PathVariable String id) {
        return ResponseEntity.ok(mobileService.getMobileById(id));
    }

    @PostMapping
    public ResponseEntity<Mobile> createMobile(@Valid @RequestBody Mobile mobile) {
        Mobile created = mobileService.createMobile(mobile);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mobile> updateMobile(@PathVariable String id, @Valid @RequestBody Mobile mobile) {
        Mobile updated = mobileService.updateMobile(id, mobile);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Mobile> assignMobile(@PathVariable String id, @Valid @RequestBody MobileAssignRequest request) {
        Mobile assigned = mobileService.assignMobile(id, request);
        return ResponseEntity.ok(assigned);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Mobile> returnMobile(@PathVariable String id, @Valid @RequestBody MobileReturnRequest request) {
        Mobile returned = mobileService.returnMobile(id, request);
        return ResponseEntity.ok(returned);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<MobileAssignment>> getMobileHistory(@PathVariable String id) {
        return ResponseEntity.ok(mobileService.getMobileHistory(id));
    }
}
