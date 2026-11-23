package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "laptop_assignments")
public class LaptopAssignment {

    @Id
    private String id;

    private String laptopId;
    private String laptopCode;
    private String laptopName;
    private String laptopSerial;

    private String employeeId;
    private String employeeName;
    private String department;

    private LocalDate assignedDate;
    private LocalDate returnedDate;
    private String status; // Active, Returned
    private String returnCondition;
    private String remarks;

    private LocalDateTime createdAt;

    public LaptopAssignment() {
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
    }

    public LaptopAssignment(String laptopId, String laptopCode, String laptopName, String laptopSerial,
                            String employeeId, String employeeName, String department,
                            LocalDate assignedDate, String remarks) {
        this();
        this.laptopId = laptopId;
        this.laptopCode = laptopCode;
        this.laptopName = laptopName;
        this.laptopSerial = laptopSerial;
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

    public String getLaptopId() {
        return laptopId;
    }

    public void setLaptopId(String laptopId) {
        this.laptopId = laptopId;
    }

    public String getLaptopCode() {
        return laptopCode;
    }

    public void setLaptopCode(String laptopCode) {
        this.laptopCode = laptopCode;
    }

    public String getLaptopName() {
        return laptopName;
    }

    public void setLaptopName(String laptopName) {
        this.laptopName = laptopName;
    }

    public String getLaptopSerial() {
        return laptopSerial;
    }

    public void setLaptopSerial(String laptopSerial) {
        this.laptopSerial = laptopSerial;
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
