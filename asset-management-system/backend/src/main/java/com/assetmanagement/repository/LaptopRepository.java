package com.assetmanagement.repository;

import com.assetmanagement.model.Laptop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaptopRepository extends MongoRepository<Laptop, String> {
    Optional<Laptop> findByLaptopId(String laptopId);
    Optional<Laptop> findBySerialNumber(String serialNumber);
    boolean existsByLaptopId(String laptopId);
    boolean existsBySerialNumber(String serialNumber);
    List<Laptop> findByStatus(String status);
    List<Laptop> findByBrand(String brand);
    Optional<Laptop> findByAssignedEmployeeId(String assignedEmployeeId);
    long countByStatus(String status);
}
