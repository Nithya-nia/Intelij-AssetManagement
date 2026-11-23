package com.assetmanagement.service;

import com.assetmanagement.exception.DuplicateResourceException;
import com.assetmanagement.exception.ResourceNotFoundException;
import com.assetmanagement.model.Recharge;
import com.assetmanagement.model.SimAssignment;
import com.assetmanagement.model.SimCard;
import com.assetmanagement.repository.RechargeRepository;
import com.assetmanagement.repository.SimAssignmentRepository;
import com.assetmanagement.repository.SimCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimCardService {

    private final SimCardRepository simCardRepository;
    private final SimAssignmentRepository simAssignmentRepository;
    private final RechargeRepository rechargeRepository;

    public SimCardService(SimCardRepository simCardRepository,
                          SimAssignmentRepository simAssignmentRepository,
                          RechargeRepository rechargeRepository) {
        this.simCardRepository = simCardRepository;
        this.simAssignmentRepository = simAssignmentRepository;
        this.rechargeRepository = rechargeRepository;
    }

    public List<SimCard> getAllSims() {
        return simCardRepository.findAll();
    }

    public SimCard getSimById(String id) {
        return simCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with id: " + id));
    }

    public SimCard getSimBySimId(String simId) {
        return simCardRepository.findBySimId(simId)
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with simId: " + simId));
    }

    public SimCard createSim(SimCard sim) {
        if (simCardRepository.existsBySimId(sim.getSimId())) {
            throw new DuplicateResourceException("SIM with ID " + sim.getSimId() + " already exists");
        }
        if (simCardRepository.existsByMobileNumber(sim.getMobileNumber())) {
            throw new DuplicateResourceException("SIM with Mobile Number " + sim.getMobileNumber() + " already exists");
        }
        if (sim.getSimSerialNumber() != null && !sim.getSimSerialNumber().trim().isEmpty()) {
            if (simCardRepository.existsBySimSerialNumber(sim.getSimSerialNumber().trim())) {
                throw new DuplicateResourceException("SIM with Serial Number " + sim.getSimSerialNumber() + " already exists");
            }
        }
        sim.setCreatedAt(LocalDateTime.now());
        sim.setUpdatedAt(LocalDateTime.now());
        return simCardRepository.save(sim);
    }

    public SimCard updateSim(String id, SimCard updatedData) {
        SimCard existing = getSimById(id);

        if (!existing.getSimId().equals(updatedData.getSimId()) &&
                simCardRepository.existsBySimId(updatedData.getSimId())) {
            throw new DuplicateResourceException("SIM with ID " + updatedData.getSimId() + " already exists");
        }
        if (!existing.getMobileNumber().equals(updatedData.getMobileNumber()) &&
                simCardRepository.existsByMobileNumber(updatedData.getMobileNumber())) {
            throw new DuplicateResourceException("SIM with Mobile Number " + updatedData.getMobileNumber() + " already exists");
        }
        if (updatedData.getSimSerialNumber() != null && !updatedData.getSimSerialNumber().trim().isEmpty()) {
            if (!updatedData.getSimSerialNumber().trim().equalsIgnoreCase(existing.getSimSerialNumber()) &&
                    simCardRepository.existsBySimSerialNumber(updatedData.getSimSerialNumber().trim())) {
                throw new DuplicateResourceException("SIM with Serial Number " + updatedData.getSimSerialNumber() + " already exists");
            }
        }

        existing.setSimId(updatedData.getSimId());
        existing.setMobileNumber(updatedData.getMobileNumber());
        existing.setProvider(updatedData.getProvider());
        existing.setSimSerialNumber(updatedData.getSimSerialNumber());
        existing.setSimType(updatedData.getSimType());

        if (!"Assigned".equalsIgnoreCase(existing.getStatus()) && !"Assigned".equalsIgnoreCase(updatedData.getStatus())) {
            existing.setStatus(updatedData.getStatus());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return simCardRepository.save(existing);
    }

    public List<SimAssignment> getSimHistory(String id) {
        SimCard sim = getSimById(id);
        return simAssignmentRepository.findBySimIdOrderByCreatedAtDesc(sim.getSimId());
    }

    public List<Recharge> getSimRecharges(String id) {
        SimCard sim = getSimById(id);
        return rechargeRepository.findBySimIdOrderByRechargeDateDesc(sim.getSimId());
    }

    public List<SimCard> searchAndFilter(String query, String provider, String status) {
        return simCardRepository.findAll().stream()
                .filter(s -> {
                    boolean matchesQuery = true;
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.toLowerCase().trim();
                        matchesQuery = (s.getMobileNumber() != null && s.getMobileNumber().toLowerCase().contains(q)) ||
                                       (s.getSimId() != null && s.getSimId().toLowerCase().contains(q)) ||
                                       (s.getProvider() != null && s.getProvider().toLowerCase().contains(q)) ||
                                       (s.getAssignedEmployeeName() != null && s.getAssignedEmployeeName().toLowerCase().contains(q)) ||
                                       (s.getAssignedMobileName() != null && s.getAssignedMobileName().toLowerCase().contains(q));
                    }
                    boolean matchesProvider = (provider == null || provider.trim().isEmpty() || "ALL".equalsIgnoreCase(provider))
                            || (s.getProvider() != null && s.getProvider().equalsIgnoreCase(provider.trim()));
                    boolean matchesStatus = (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status))
                            || (s.getStatus() != null && s.getStatus().equalsIgnoreCase(status.trim()));

                    return matchesQuery && matchesProvider && matchesStatus;
                })
                .collect(Collectors.toList());
    }
}
