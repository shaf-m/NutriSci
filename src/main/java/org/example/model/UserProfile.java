package org.example.model;

import java.sql.Date;
import java.time.LocalDate;

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

    public int getAge() {
        return LocalDate.now().getYear() - this.getDateOfBirth().toLocalDate().getYear();
    }

    public double calculateBMR() {
        int age = this.getAge();

        double bmr;

        // I think this is the right formula based on what I found online (compared and seemed ok)
        if (getSex().equalsIgnoreCase("Male")) {
            bmr = 10 * this.getWeightKg() + 6.25 * this.getHeightCm() - 5 * age + 5;
        } else if (getSex().equalsIgnoreCase("Female")) {
            bmr = 10 * this.getWeightKg() + 6.25 * this.getHeightCm() - 5 * age - 161;
        } else {
            bmr = 10 * this.getWeightKg() + 6.25 * this.getHeightCm() - 5 * age;
        }

        return bmr;
    }

    public String getHTML() {
        return String.format(
                "<html><div style='font-family:sans-serif; text-align:center;'>"
                        + "<br><br>"
                        + "<p style='font-size:10px; color:gray; margin-bottom:10px;'>"
                        + "Basal Metabolic Rate (BMR) is the number of calories your body burns at rest to maintain vital functions like breathing and circulation."
                        + "</p>"
                        + "<h3>BMR for %s</h3>"
                        + "<div style='display:inline-block; text-align:left; margin-left:20px;'>"
                        + "<ul style='padding-left:20px;'>"
                        + "<li>Sex: %s</li>"
                        + "<li>Age: %d</li>"
                        + "<li>Height: %.1f cm</li>"
                        + "<li>Weight: %.1f kg</li>"
                        + "</ul></div><br>"
                        + "<b>Estimated BMR: %.0f calories/day</b>"
                        + "</div></html>",
                getName(), getSex(), getAge(), getHeightCm(), getWeightKg(), calculateBMR()
        );
    }
}
