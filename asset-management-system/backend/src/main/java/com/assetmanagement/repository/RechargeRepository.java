package com.assetmanagement.repository;

import com.assetmanagement.model.Recharge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RechargeRepository extends MongoRepository<Recharge, String> {
    List<Recharge> findBySimIdOrderByRechargeDateDesc(String simId);
    List<Recharge> findByMobileNumberOrderByRechargeDateDesc(String mobileNumber);
    List<Recharge> findByEmployeeIdOrderByRechargeDateDesc(String employeeId);
    List<Recharge> findByProvider(String provider);
    List<Recharge> findAllByOrderByRechargeDateDesc();
    Optional<Recharge> findFirstBySimIdOrderByRechargeDateDesc(String simId);
    List<Recharge> findByPlanEndDateBetweenOrderByPlanEndDateAsc(LocalDate start, LocalDate end);
    List<Recharge> findByPlanEndDateBeforeOrderByPlanEndDateAsc(LocalDate date);
}
