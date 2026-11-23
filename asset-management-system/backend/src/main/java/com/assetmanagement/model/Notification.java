package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String simId;
    private String mobileNumber;
    private String provider;
    private String employeeName;

    private Double rechargeAmount;
    private LocalDate rechargeDate;
    private LocalDate planEndDate;
    private long remainingDays;
    private String expiryStatus; // Normal, Expiring Soon, Expires Today, Expired

    private LocalDateTime calculatedAt;

    public Notification() {
        this.calculatedAt = LocalDateTime.now();
    }

    public Notification(String simId, String mobileNumber, String provider, String employeeName,
                        Double rechargeAmount, LocalDate rechargeDate, LocalDate planEndDate,
                        long remainingDays, String expiryStatus) {
        this();
        this.simId = simId;
        this.mobileNumber = mobileNumber;
        this.provider = provider;
        this.employeeName = employeeName;
        this.rechargeAmount = rechargeAmount;
        this.rechargeDate = rechargeDate;
        this.planEndDate = planEndDate;
        this.remainingDays = remainingDays;
        this.expiryStatus = expiryStatus;
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

    public long getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(long remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getExpiryStatus() {
        return expiryStatus;
    }

    public void setExpiryStatus(String expiryStatus) {
        this.expiryStatus = expiryStatus;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
