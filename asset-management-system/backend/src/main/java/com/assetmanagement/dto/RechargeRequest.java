package com.assetmanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RechargeRequest {

    @NotBlank(message = "SIM ID is required")
    private String simId;

    @NotNull(message = "Recharge Amount is required")
    @DecimalMin(value = "0.01", message = "Recharge amount must be greater than zero")
    private Double rechargeAmount;

    @NotBlank(message = "Recharge Plan is required")
    private String rechargePlan;

    @NotNull(message = "Recharge Date is required")
    private LocalDate rechargeDate;

    @NotNull(message = "Plan End Date is required")
    private LocalDate planEndDate;

    private String remarks;

    public RechargeRequest() {
    }

    public RechargeRequest(String simId, Double rechargeAmount, String rechargePlan,
                           LocalDate rechargeDate, LocalDate planEndDate, String remarks) {
        this.simId = simId;
        this.rechargeAmount = rechargeAmount;
        this.rechargePlan = rechargePlan;
        this.rechargeDate = rechargeDate;
        this.planEndDate = planEndDate;
        this.remarks = remarks;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getRechargePlan() {
        return rechargePlan;
    }

    public void setRechargePlan(String rechargePlan) {
        this.rechargePlan = rechargePlan;
    }

    public LocalDate getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(LocalDate rechargeDate) {
        this.rechargeDate = rechargeDate;
    }

    public LocalDate getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
