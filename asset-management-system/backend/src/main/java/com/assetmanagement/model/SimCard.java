package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "sim_cards")
public class SimCard {

    @Id
    private String id;

    @Indexed(unique = true)
    private String simId;

    @Indexed(unique = true)
    private String mobileNumber;

    private String provider; // Airtel, Jio, Vi, BSNL, Other

    private String simSerialNumber;

    private String simType; // Physical SIM, eSIM
    private String status; // Available, Assigned, Inactive

    private String assignedEmployeeId;
    private String assignedEmployeeName;
    private String assignedMobileId;
    private String assignedMobileName;

    // Cached info about latest recharge for convenience and queries
    private Double latestRechargeAmount;
    private String latestRechargePlan;
    private LocalDate latestRechargeDate;
    private LocalDate latestPlanEndDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SimCard() {
        this.status = "Available";
        this.simType = "Physical SIM";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public SimCard(String simId, String mobileNumber, String provider, String simSerialNumber, String simType, String status) {
        this();
        this.simId = simId;
        this.mobileNumber = mobileNumber;
        this.provider = provider;
        this.simSerialNumber = simSerialNumber;
        if (simType != null && !simType.isEmpty()) {
            this.simType = simType;
        }
        if (status != null && !status.isEmpty()) {
            this.status = status;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getSimType() {
        return simType;
    }

    public void setSimType(String simType) {
        this.simType = simType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public String getAssignedEmployeeName() {
        return assignedEmployeeName;
    }

    public void setAssignedEmployeeName(String assignedEmployeeName) {
        this.assignedEmployeeName = assignedEmployeeName;
    }

    public String getAssignedMobileId() {
        return assignedMobileId;
    }

    public void setAssignedMobileId(String assignedMobileId) {
        this.assignedMobileId = assignedMobileId;
    }

    public String getMappedMobileId() {
        return assignedMobileId;
    }

    public void setMappedMobileId(String mappedMobileId) {
        this.assignedMobileId = mappedMobileId;
    }

    public String getAssignedMobileName() {
        return assignedMobileName;
    }

    public void setAssignedMobileName(String assignedMobileName) {
        this.assignedMobileName = assignedMobileName;
    }

    public String getMappedMobileName() {
        return assignedMobileName;
    }

    public void setMappedMobileName(String mappedMobileName) {
        this.assignedMobileName = mappedMobileName;
    }

    public Double getLatestRechargeAmount() {
        return latestRechargeAmount;
    }

    public void setLatestRechargeAmount(Double latestRechargeAmount) {
        this.latestRechargeAmount = latestRechargeAmount;
    }

    public String getLatestRechargePlan() {
        return latestRechargePlan;
    }

    public void setLatestRechargePlan(String latestRechargePlan) {
        this.latestRechargePlan = latestRechargePlan;
    }

    public LocalDate getLatestRechargeDate() {
        return latestRechargeDate;
    }

    public void setLatestRechargeDate(LocalDate latestRechargeDate) {
        this.latestRechargeDate = latestRechargeDate;
    }

    public LocalDate getLatestPlanEndDate() {
        return latestPlanEndDate;
    }

    public void setLatestPlanEndDate(LocalDate latestPlanEndDate) {
        this.latestPlanEndDate = latestPlanEndDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
