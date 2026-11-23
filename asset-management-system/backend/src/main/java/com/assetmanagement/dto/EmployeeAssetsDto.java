package com.assetmanagement.dto;

import com.assetmanagement.model.Laptop;
import com.assetmanagement.model.Mobile;
import com.assetmanagement.model.SimCard;

import java.util.List;

public class EmployeeAssetsDto {

    private String employeeId;
    private String employeeName;
    private Laptop assignedLaptop;
    private Mobile assignedMobile;
    private List<SimCard> assignedSims;
    private List<AssetHistoryDto> history;

    public EmployeeAssetsDto() {
    }

    public EmployeeAssetsDto(String employeeId, String employeeName, Laptop assignedLaptop,
                             Mobile assignedMobile, List<SimCard> assignedSims, List<AssetHistoryDto> history) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.assignedLaptop = assignedLaptop;
        this.assignedMobile = assignedMobile;
        this.assignedSims = assignedSims;
        this.history = history;
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

    public Laptop getAssignedLaptop() {
        return assignedLaptop;
    }

    public void setAssignedLaptop(Laptop assignedLaptop) {
        this.assignedLaptop = assignedLaptop;
    }

    public Mobile getAssignedMobile() {
        return assignedMobile;
    }

    public void setAssignedMobile(Mobile assignedMobile) {
        this.assignedMobile = assignedMobile;
    }

    public List<SimCard> getAssignedSims() {
        return assignedSims;
    }

    public void setAssignedSims(List<SimCard> assignedSims) {
        this.assignedSims = assignedSims;
    }

    public List<AssetHistoryDto> getHistory() {
        return history;
    }

    public void setHistory(List<AssetHistoryDto> history) {
        this.history = history;
    }
}
