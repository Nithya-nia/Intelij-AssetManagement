package com.assetmanagement.repository;

import com.assetmanagement.model.SimAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimAssignmentRepository extends MongoRepository<SimAssignment, String> {
    List<SimAssignment> findBySimIdOrderByCreatedAtDesc(String simId);
    List<SimAssignment> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);
    List<SimAssignment> findByMobileIdOrderByCreatedAtDesc(String mobileId);
    Optional<SimAssignment> findBySimIdAndStatus(String simId, String status);
    List<SimAssignment> findByEmployeeIdAndStatus(String employeeId, String status);
    List<SimAssignment> findByMobileIdAndStatus(String mobileId, String status);
    long countByStatus(String status);
    List<SimAssignment> findTop5ByOrderByCreatedAtDesc();
    List<SimAssignment> findAllByOrderByCreatedAtDesc();
}
