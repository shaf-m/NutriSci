package org.example.model;

import java.sql.Date;

public class MealLogFactory extends HealthLogFactory {
    public MealLogFactory() { }

    public HealthLog createLog(int profileId, Date mealDate, String mealType, int foodId, double quantity) {
        return new MealLog(profileId, mealDate, mealType, foodId, quantity);
    }

    public HealthLog createLog(int mealId, int profileId, Date mealDate, String mealType, int foodId, double quantity) {
        return new MealLog(mealId, profileId, mealDate, mealType, foodId, quantity);
    }
}
