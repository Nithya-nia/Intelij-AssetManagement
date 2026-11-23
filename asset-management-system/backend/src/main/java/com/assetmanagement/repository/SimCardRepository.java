package com.assetmanagement.repository;

import com.assetmanagement.model.SimCard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimCardRepository extends MongoRepository<SimCard, String> {
    Optional<SimCard> findBySimId(String simId);
    Optional<SimCard> findByMobileNumber(String mobileNumber);
    Optional<SimCard> findBySimSerialNumber(String simSerialNumber);
    boolean existsBySimId(String simId);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsBySimSerialNumber(String simSerialNumber);
    List<SimCard> findByStatus(String status);
    List<SimCard> findByProvider(String provider);
    List<SimCard> findByAssignedEmployeeId(String assignedEmployeeId);
    List<SimCard> findByAssignedMobileId(String assignedMobileId);
    long countByStatus(String status);
}
