package org.example.model;

import java.sql.Date;

public class UserProfile {
    private int profileID;
    private String name;
    private String sex;
    private Date dateOfBirth;
    private double heightCm;
    private double weightKg;
    private String units;

    // Constructors, Getters, Setters
    public UserProfile() {}

    public UserProfile(String name, String sex, Date dateOfBirth, double heightCm, double weightKg, String units) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.units = units;
    }

    public UserProfile(int profileID, String name, String sex, Date dateOfBirth, double heightCm, double weightKg, String units) {
        this.profileID = profileID;
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.units = units;
    }

    public int getProfileID() { return profileID; }
    public void setProfileID(int profileID) { this.profileID = profileID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }
}
