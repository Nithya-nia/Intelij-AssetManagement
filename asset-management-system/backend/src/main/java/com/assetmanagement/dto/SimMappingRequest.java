package com.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SimMappingRequest {

    @NotBlank(message = "Mobile ID is required")
    private String mobileId;

    @NotBlank(message = "SIM ID is required")
    private String simId;

    @NotNull(message = "Slot number is required (1 or 2)")
    private Integer slotNumber;

    private String remarks;

    public SimMappingRequest() {
    }

    public SimMappingRequest(String mobileId, String simId, Integer slotNumber, String remarks) {
        this.mobileId = mobileId;
        this.simId = simId;
        this.slotNumber = slotNumber;
        this.remarks = remarks;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
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
