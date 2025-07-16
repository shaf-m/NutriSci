package org.example.model;

import java.sql.Date;

public class ExerciseLogFactory extends HealthLogFactory{
    public ExerciseLogFactory() { }

    public ExerciseLog createLog(int profileId, java.sql.Date exerciseDate, String exerciseType, int duration) {
        return new ExerciseLog(profileId, exerciseDate, exerciseType, duration);
    }

    public ExerciseLog createLog(int exerciseId, int profileId, Date exerciseDate, String exerciseType, int duration) {
        return new ExerciseLog(exerciseId, profileId, exerciseDate, exerciseType, duration);
    }
}
