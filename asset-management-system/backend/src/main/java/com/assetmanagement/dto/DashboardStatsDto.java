package com.assetmanagement.dto;

import com.assetmanagement.model.Employee;
import com.assetmanagement.model.Notification;

import java.util.List;

public class DashboardStatsDto {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;

    private long totalLaptops;
    private long assignedLaptops;
    private long availableLaptops;
    private long underRepairLaptops;
    private long retiredLaptops;

    private long totalMobiles;
    private long assignedMobiles;
    private long availableMobiles;
    private long underRepairMobiles;
    private long retiredMobiles;

    private long totalSims;
    private long assignedSims;
    private long availableSims;
    private long inactiveSims;

    private long activeSimAssignments;
    private long expiringSoonSims;
    private long expiredSims;

    private List<Employee> recentEmployees;
    private List<AssetHistoryDto> recentAssignments;
    private List<Notification> simExpiryAlerts;

    public DashboardStatsDto() {
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public long getInactiveEmployees() {
        return inactiveEmployees;
    }

    public void setInactiveEmployees(long inactiveEmployees) {
        this.inactiveEmployees = inactiveEmployees;
    }

    public long getTotalLaptops() {
        return totalLaptops;
    }

    public void setTotalLaptops(long totalLaptops) {
        this.totalLaptops = totalLaptops;
    }

    public long getAssignedLaptops() {
        return assignedLaptops;
    }

    public void setAssignedLaptops(long assignedLaptops) {
        this.assignedLaptops = assignedLaptops;
    }

    public long getAvailableLaptops() {
        return availableLaptops;
    }

    public void setAvailableLaptops(long availableLaptops) {
        this.availableLaptops = availableLaptops;
    }

    public long getUnderRepairLaptops() {
        return underRepairLaptops;
    }

    public void setUnderRepairLaptops(long underRepairLaptops) {
        this.underRepairLaptops = underRepairLaptops;
    }

    public long getRetiredLaptops() {
        return retiredLaptops;
    }

    public void setRetiredLaptops(long retiredLaptops) {
        this.retiredLaptops = retiredLaptops;
    }

    public long getTotalMobiles() {
        return totalMobiles;
    }

    public void setTotalMobiles(long totalMobiles) {
        this.totalMobiles = totalMobiles;
    }

    public long getAssignedMobiles() {
        return assignedMobiles;
    }

    public void setAssignedMobiles(long assignedMobiles) {
        this.assignedMobiles = assignedMobiles;
    }

    public long getAvailableMobiles() {
        return availableMobiles;
    }

    public void setAvailableMobiles(long availableMobiles) {
        this.availableMobiles = availableMobiles;
    }

    public long getUnderRepairMobiles() {
        return underRepairMobiles;
    }

    public void setUnderRepairMobiles(long underRepairMobiles) {
        this.underRepairMobiles = underRepairMobiles;
    }

    public long getRetiredMobiles() {
        return retiredMobiles;
    }

    public void setRetiredMobiles(long retiredMobiles) {
        this.retiredMobiles = retiredMobiles;
    }

    public long getTotalSims() {
        return totalSims;
    }

    public void setTotalSims(long totalSims) {
        this.totalSims = totalSims;
    }

    public long getAssignedSims() {
        return assignedSims;
    }

    public void setAssignedSims(long assignedSims) {
        this.assignedSims = assignedSims;
    }

    public long getAvailableSims() {
        return availableSims;
    }

    public void setAvailableSims(long availableSims) {
        this.availableSims = availableSims;
    }

    public long getInactiveSims() {
        return inactiveSims;
    }

    public void setInactiveSims(long inactiveSims) {
        this.inactiveSims = inactiveSims;
    }

    public long getActiveSimAssignments() {
        return activeSimAssignments;
    }

    public void setActiveSimAssignments(long activeSimAssignments) {
        this.activeSimAssignments = activeSimAssignments;
    }

    public long getExpiringSoonSims() {
        return expiringSoonSims;
    }

    public void setExpiringSoonSims(long expiringSoonSims) {
        this.expiringSoonSims = expiringSoonSims;
    }

    public long getExpiredSims() {
        return expiredSims;
    }

    public void setExpiredSims(long expiredSims) {
        this.expiredSims = expiredSims;
    }

    public List<Employee> getRecentEmployees() {
        return recentEmployees;
    }

    public void setRecentEmployees(List<Employee> recentEmployees) {
        this.recentEmployees = recentEmployees;
    }

    public List<AssetHistoryDto> getRecentAssignments() {
        return recentAssignments;
    }

    public void setRecentAssignments(List<AssetHistoryDto> recentAssignments) {
        this.recentAssignments = recentAssignments;
    }

    public List<Notification> getSimExpiryAlerts() {
        return simExpiryAlerts;
    }

    public void setSimExpiryAlerts(List<Notification> simExpiryAlerts) {
        this.simExpiryAlerts = simExpiryAlerts;
    }
}
