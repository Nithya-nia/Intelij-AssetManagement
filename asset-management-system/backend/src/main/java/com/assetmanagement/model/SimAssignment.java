package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "sim_assignments")
public class SimAssignment {

    @Id
    private String id;

    private String simId;
    private String simCode;
    private String mobileNumber;
    private String provider;

    private String employeeId;
    private String employeeName;

    private String mobileId;
    private String mobileName;

    private LocalDate issuedDate;
    private LocalDate returnedDate;
    private Integer slotNumber; // 1 (Primary Slot) or 2 (Secondary Slot)
    private String status; // Active, Returned
    private String remarks;

    private LocalDateTime createdAt;

    public SimAssignment() {
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
    }

    public SimAssignment(String simId, String simCode, String mobileNumber, String provider,
                         String employeeId, String employeeName,
                         String mobileId, String mobileName,
                         LocalDate issuedDate, String remarks) {
        this();
        this.simId = simId;
        this.simCode = simCode;
        this.mobileNumber = mobileNumber;
        this.provider = provider;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.mobileId = mobileId;
        this.mobileName = mobileName;
        this.issuedDate = issuedDate;
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

    public String getSimCode() {
        return simCode;
    }

    public void setSimCode(String simCode) {
        this.simCode = simCode;
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

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public Integer getSlotNumber() {
        return slotNumber != null ? slotNumber : 1;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
