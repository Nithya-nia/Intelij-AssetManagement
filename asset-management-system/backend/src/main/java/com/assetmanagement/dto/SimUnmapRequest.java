package com.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class SimUnmapRequest {

    @NotBlank(message = "Mobile ID is required")
    private String mobileId;

    private Integer slotNumber;
    private String simId;
    private String remarks;

    public SimUnmapRequest() {
    }

    public SimUnmapRequest(String mobileId, Integer slotNumber, String simId, String remarks) {
        this.mobileId = mobileId;
        this.slotNumber = slotNumber;
        this.simId = simId;
        this.remarks = remarks;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
