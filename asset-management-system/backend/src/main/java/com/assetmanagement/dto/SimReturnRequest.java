package com.assetmanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SimReturnRequest {

    @NotNull(message = "Returned Date is required")
    private LocalDate returnedDate;

    private String remarks;

    public SimReturnRequest() {
    }

    public SimReturnRequest(LocalDate returnedDate, String remarks) {
        this.returnedDate = returnedDate;
        this.remarks = remarks;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
