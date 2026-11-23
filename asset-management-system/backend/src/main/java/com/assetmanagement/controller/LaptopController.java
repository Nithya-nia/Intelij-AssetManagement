package com.assetmanagement.controller;

import com.assetmanagement.dto.LaptopAssignRequest;
import com.assetmanagement.dto.LaptopReturnRequest;
import com.assetmanagement.model.Laptop;
import com.assetmanagement.model.LaptopAssignment;
import com.assetmanagement.service.LaptopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {

    private final LaptopService laptopService;

    public LaptopController(LaptopService laptopService) {
        this.laptopService = laptopService;
    }

    @GetMapping
    public ResponseEntity<List<Laptop>> getLaptops(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String project) {
        if (search != null || brand != null || status != null || project != null) {
            return ResponseEntity.ok(laptopService.searchAndFilter(search, brand, status, project));
        }
        return ResponseEntity.ok(laptopService.getAllLaptops());
    }

    @GetMapping("/next-id")
    public ResponseEntity<java.util.Map<String, String>> getNextLaptopId() {
        return ResponseEntity.ok(java.util.Map.of("nextId", laptopService.peekNextLaptopId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Laptop> getLaptopById(@PathVariable String id) {
        return ResponseEntity.ok(laptopService.getLaptopById(id));
    }

    @PostMapping
    public ResponseEntity<Laptop> createLaptop(@Valid @RequestBody Laptop laptop) {
        Laptop created = laptopService.createLaptop(laptop);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Laptop> updateLaptop(@PathVariable String id, @Valid @RequestBody Laptop laptop) {
        Laptop updated = laptopService.updateLaptop(id, laptop);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Laptop> assignLaptop(@PathVariable String id, @Valid @RequestBody LaptopAssignRequest request) {
        Laptop assigned = laptopService.assignLaptop(id, request);
        return ResponseEntity.ok(assigned);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Laptop> returnLaptop(@PathVariable String id, @Valid @RequestBody LaptopReturnRequest request) {
        Laptop returned = laptopService.returnLaptop(id, request);
        return ResponseEntity.ok(returned);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<LaptopAssignment>> getLaptopHistory(@PathVariable String id) {
        return ResponseEntity.ok(laptopService.getLaptopHistory(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaptop(@PathVariable String id) {
        laptopService.deleteLaptop(id);
        return ResponseEntity.noContent().build();
    }
}
