package com.assetmanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LaptopReturnRequest {

    @NotNull(message = "Returned Date is required")
    private LocalDate returnedDate;

    private String returnCondition; // Good, Damaged, Minor Scratches, etc.
    private String remarks;

    public LaptopReturnRequest() {
    }

    public LaptopReturnRequest(LocalDate returnedDate, String returnCondition, String remarks) {
        this.returnedDate = returnedDate;
        this.returnCondition = returnCondition;
        this.remarks = remarks;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
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
