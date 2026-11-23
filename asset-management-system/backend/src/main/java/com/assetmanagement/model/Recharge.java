package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "recharges")
public class Recharge {

    @Id
    private String id;

    private String simId;
    private String mobileNumber;
    private String provider;

    private String employeeId;
    private String employeeName;

    private Double rechargeAmount;
    private String rechargePlan;
    private LocalDate rechargeDate;
    private LocalDate planEndDate;
    private String remarks;

    private LocalDateTime createdAt;

    public Recharge() {
        this.createdAt = LocalDateTime.now();
    }

    public Recharge(String simId, String mobileNumber, String provider,
                    String employeeId, String employeeName,
                    Double rechargeAmount, String rechargePlan,
                    LocalDate rechargeDate, LocalDate planEndDate, String remarks) {
        this();
        this.simId = simId;
        this.mobileNumber = mobileNumber;
        this.provider = provider;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.rechargeAmount = rechargeAmount;
        this.rechargePlan = rechargePlan;
        this.rechargeDate = rechargeDate;
        this.planEndDate = planEndDate;
        this.remarks = remarks;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getRechargePlan() {
        return rechargePlan;
    }

    public void setRechargePlan(String rechargePlan) {
        this.rechargePlan = rechargePlan;
    }

    public LocalDate getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(LocalDate rechargeDate) {
        this.rechargeDate = rechargeDate;
    }

    public LocalDate getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
