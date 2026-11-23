package com.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LaptopAssignRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Assigned Date is required")
    private LocalDate assignedDate;

    private String remarks;

    public LaptopAssignRequest() {
    }

    public LaptopAssignRequest(String employeeId, LocalDate assignedDate, String remarks) {
        this.employeeId = employeeId;
        this.assignedDate = assignedDate;
        this.remarks = remarks;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
