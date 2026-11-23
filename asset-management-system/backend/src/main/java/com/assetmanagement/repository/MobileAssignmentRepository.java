package com.assetmanagement.repository;

import com.assetmanagement.model.MobileAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MobileAssignmentRepository extends MongoRepository<MobileAssignment, String> {
    List<MobileAssignment> findByMobileIdOrderByCreatedAtDesc(String mobileId);
    List<MobileAssignment> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);
    Optional<MobileAssignment> findByMobileIdAndStatus(String mobileId, String status);
    Optional<MobileAssignment> findByEmployeeIdAndStatus(String employeeId, String status);
    List<MobileAssignment> findTop5ByOrderByCreatedAtDesc();
    List<MobileAssignment> findAllByOrderByCreatedAtDesc();
}
