package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "mobile_assignments")
public class MobileAssignment {

    @Id
    private String id;

    private String mobileId;
    private String mobileCode;
    private String mobileName;
    private String imeiNumber;

    private String employeeId;
    private String employeeName;
    private String department;

    private LocalDate assignedDate;
    private LocalDate returnedDate;
    private String status; // Active, Returned
    private String returnCondition;
    private String remarks;

    private LocalDateTime createdAt;

    public MobileAssignment() {
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
    }

    public MobileAssignment(String mobileId, String mobileCode, String mobileName, String imeiNumber,
                            String employeeId, String employeeName, String department,
                            LocalDate assignedDate, String remarks) {
        this();
        this.mobileId = mobileId;
        this.mobileCode = mobileCode;
        this.mobileName = mobileName;
        this.imeiNumber = imeiNumber;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.assignedDate = assignedDate;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
