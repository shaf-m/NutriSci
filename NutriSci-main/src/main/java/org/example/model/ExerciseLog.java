package org.example.model;

import java.sql.Date;

public class ExerciseLog extends HealthLog {

    public ExerciseLog() { super(); }

    public ExerciseLog(int profileId, java.sql.Date exerciseDate, String exerciseType, int duration) {
        super(profileId, exerciseDate, exerciseType, duration);
    }

    public ExerciseLog(int exerciseId, int profileId, Date exerciseDate, String exerciseType, int duration) {
        super(exerciseId, profileId, exerciseDate, exerciseType, duration);
    }

    public int getDurationMinutes() {
        return (int)getQuantity();
    }

    public void setDurationMinutes(int durationMinutes) {
        setQuantity(durationMinutes);
    }

}
