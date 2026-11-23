package com.assetmanagement.repository;

import com.assetmanagement.model.LaptopAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaptopAssignmentRepository extends MongoRepository<LaptopAssignment, String> {
    List<LaptopAssignment> findByLaptopIdOrderByCreatedAtDesc(String laptopId);
    List<LaptopAssignment> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);
    Optional<LaptopAssignment> findByLaptopIdAndStatus(String laptopId, String status);
    Optional<LaptopAssignment> findByEmployeeIdAndStatus(String employeeId, String status);
    List<LaptopAssignment> findTop5ByOrderByCreatedAtDesc();
    List<LaptopAssignment> findAllByOrderByCreatedAtDesc();
}
