package org.example.model;

import java.util.Date;

public abstract class HealthLog {
    private int logId;
    private int profileId;
    private Date logDate;
    private String logType;
    private double calories;
    private double quantity; //Quantity or duration

    public HealthLog() { }

    public HealthLog(int profileId, java.sql.Date logDate, String logType, double quantity) {
        this.profileId = profileId;
        this.logDate = logDate;
        this.logType = logType;
        this.quantity = quantity;
    }

    public HealthLog(int logId, int profileId, java.sql.Date logDate, String logType, double quantity) {
        this.logId = logId;
        this.profileId = profileId;
        this.logDate = logDate;
        this.logType = logType;
        this.quantity = quantity;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
