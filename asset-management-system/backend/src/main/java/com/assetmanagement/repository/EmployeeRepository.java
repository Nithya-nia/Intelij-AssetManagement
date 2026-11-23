package com.assetmanagement.repository;

import com.assetmanagement.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
    List<Employee> findByStatus(String status);
    List<Employee> findByDepartment(String department);
    List<Employee> findTop5ByOrderByCreatedAtDesc();
}
