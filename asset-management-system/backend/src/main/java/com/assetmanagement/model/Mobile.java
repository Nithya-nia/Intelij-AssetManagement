package com.assetmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "mobiles")
public class Mobile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String mobileId;

    private String name; // Model
    private String brand;

    @Indexed(unique = true)
    private String imeiNumber;

    private String projectName; // Required Project Name
    private String simSlotConfig; // "Single SIM" or "Dual SIM"

    private LocalDate purchaseDate;
    private LocalDate warrantyExpiryDate;
    private String status; // Available, Assigned, Under Repair, Retired

    private String assignedEmployeeId;
    private String assignedEmployeeName;
    private LocalDate assignedDate;
    private int activeSimCount;

    // Explicit Slot 1 and Slot 2 mapping (SIM numbers belong to SIM cards)
    private String slot1SimId;
    private String slot1MobileNumber;
    private String slot1Provider;
    private String slot2SimId;
    private String slot2MobileNumber;
    private String slot2Provider;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Mobile() {
        this.status = "Available";
        this.simSlotConfig = "Dual SIM";
        this.activeSimCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Mobile(String mobileId, String name, String brand, String imeiNumber,
                  String projectName, String simSlotConfig,
                  LocalDate purchaseDate, LocalDate warrantyExpiryDate, String status) {
        this();
        this.mobileId = mobileId;
        this.name = name;
        this.brand = brand;
        this.imeiNumber = imeiNumber;
        this.projectName = projectName;
        if (simSlotConfig != null && !simSlotConfig.isEmpty()) {
            this.simSlotConfig = simSlotConfig;
        }
        this.purchaseDate = purchaseDate;
        this.warrantyExpiryDate = warrantyExpiryDate;
        if (status != null && !status.isEmpty()) {
            this.status = status;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSimSlotConfig() {
        return simSlotConfig;
    }

    public void setSimSlotConfig(String simSlotConfig) {
        this.simSlotConfig = simSlotConfig;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public String getAssignedEmployeeName() {
        return assignedEmployeeName;
    }

    public void setAssignedEmployeeName(String assignedEmployeeName) {
        this.assignedEmployeeName = assignedEmployeeName;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public int getActiveSimCount() {
        return activeSimCount;
    }

    public void setActiveSimCount(int activeSimCount) {
        this.activeSimCount = activeSimCount;
    }

    public String getSlot1SimId() {
        return slot1SimId;
    }

    public void setSlot1SimId(String slot1SimId) {
        this.slot1SimId = slot1SimId;
    }

    public String getSlot1MobileNumber() {
        return slot1MobileNumber;
    }

    public void setSlot1MobileNumber(String slot1MobileNumber) {
        this.slot1MobileNumber = slot1MobileNumber;
    }

    public String getSlot1Provider() {
        return slot1Provider;
    }

    public void setSlot1Provider(String slot1Provider) {
        this.slot1Provider = slot1Provider;
    }

    public String getSlot2SimId() {
        return slot2SimId;
    }

    public void setSlot2SimId(String slot2SimId) {
        this.slot2SimId = slot2SimId;
    }

    public String getSlot2MobileNumber() {
        return slot2MobileNumber;
    }

    public void setSlot2MobileNumber(String slot2MobileNumber) {
        this.slot2MobileNumber = slot2MobileNumber;
    }

    public String getSlot2Provider() {
        return slot2Provider;
    }

    public void setSlot2Provider(String slot2Provider) {
        this.slot2Provider = slot2Provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
