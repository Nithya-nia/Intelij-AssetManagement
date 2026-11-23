package com.assetmanagement.dto;

import java.time.LocalDate;

public class AssetHistoryDto {

    private String id;
    private String employeeName;
    private String employeeId;
    private String assetType; // Laptop, Mobile, SIM
    private String assetName;
    private String assetIdentifier; // Serial Number / IMEI / SIM Mobile Number
    private String provider; // For SIM
    private LocalDate assignedDate;
    private LocalDate returnedDate;
    private String assignmentStatus; // Active, Returned
    private String returnCondition;
    private String remarks;

    public AssetHistoryDto() {
    }

    public AssetHistoryDto(String id, String employeeName, String employeeId, String assetType,
                           String assetName, String assetIdentifier, String provider,
                           LocalDate assignedDate, LocalDate returnedDate,
                           String assignmentStatus, String returnCondition, String remarks) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.assetType = assetType;
        this.assetName = assetName;
        this.assetIdentifier = assetIdentifier;
        this.provider = provider;
        this.assignedDate = assignedDate;
        this.returnedDate = returnedDate;
        this.assignmentStatus = assignmentStatus;
        this.returnCondition = returnCondition;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetIdentifier() {
        return assetIdentifier;
    }

    public void setAssetIdentifier(String assetIdentifier) {
        this.assetIdentifier = assetIdentifier;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getReturnCondition() {
        return returnCondition;
    }

    public void setReturnCondition(String returnCondition) {
        this.returnCondition = returnCondition;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
