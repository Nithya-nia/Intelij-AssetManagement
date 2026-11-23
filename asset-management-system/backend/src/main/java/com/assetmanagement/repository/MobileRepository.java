package com.assetmanagement.repository;

import com.assetmanagement.model.Mobile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MobileRepository extends MongoRepository<Mobile, String> {
    Optional<Mobile> findByMobileId(String mobileId);
    Optional<Mobile> findByImeiNumber(String imeiNumber);
    boolean existsByMobileId(String mobileId);
    boolean existsByImeiNumber(String imeiNumber);
    List<Mobile> findByStatus(String status);
    List<Mobile> findByBrand(String brand);
    Optional<Mobile> findByAssignedEmployeeId(String assignedEmployeeId);
    long countByStatus(String status);
}
