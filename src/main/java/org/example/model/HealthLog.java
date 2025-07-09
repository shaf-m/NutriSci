package org.example.model;

import java.util.Date;


public abstract class HealthLog {
    private int profileId;
    private Date logDate;

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


    public final void displayLog() {
        System.out.println("Profile ID: " + profileId);
        System.out.println("Date: " + logDate);
        displaySpecificDetails();
    }


    protected abstract void displaySpecificDetails();
}

