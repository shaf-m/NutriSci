package org.example.model;

import java.util.Date;

public class ExerciseLog extends HealthLog {
    private int exerciseId;
    private String exerciseType;
    private int durationMinutes;
    private double caloriesBurned;

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    @Override
    protected void displaySpecificDetails() {
        System.out.println("Exercise ID: " + exerciseId);
        System.out.println("Exercise Type: " + exerciseType);
        System.out.println("Duration (minutes): " + durationMinutes);
        System.out.println("Calories Burned: " + caloriesBurned);
    }
}
