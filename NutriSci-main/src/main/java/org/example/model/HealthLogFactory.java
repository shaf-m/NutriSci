package org.example.model;

import java.sql.Date;

public abstract class HealthLogFactory implements LogFactory {
    public HealthLog createLog(String type) {
        if (type.equals("exercise")) {
            return new ExerciseLog();
        }
        else {
            return new MealLog();
        }
    }
}
