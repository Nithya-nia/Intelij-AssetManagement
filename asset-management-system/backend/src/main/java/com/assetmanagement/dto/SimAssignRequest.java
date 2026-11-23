package com.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SimAssignRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Mobile Phone ID is required")
    private String mobileId;

    @NotNull(message = "Issued Date is required")
    private LocalDate issuedDate;

    private Integer slotNumber; // Explicit Slot 1 or Slot 2 mapping
    private String remarks;

    public SimAssignRequest() {
    }

    public SimAssignRequest(String employeeId, String mobileId, LocalDate issuedDate, String remarks) {
        this.employeeId = employeeId;
        this.mobileId = mobileId;
        this.issuedDate = issuedDate;
        this.remarks = remarks;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
