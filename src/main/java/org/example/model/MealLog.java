package org.example.model;

import java.sql.Date;

public class MealLog {
    private int mealId;
    private int profileId;
    private Date mealDate;
    private String mealType;
    private int foodId;
    private String foodName;
    private double quantity;

    // Nutrition fields
    private double calories;
    private double fat;
    private double saturatedFat;
    private double transFat;
    private double carbohydrates;
    private double fiber;
    private double sugars;
    private double protein;
    private double cholesterol;
    private double sodium;
    private double potassium;
    private double calcium;
    private double iron;

    // Constructors
    public MealLog() {}

    public MealLog(int profileId, Date mealDate, String mealType, int foodId, double quantity) {
        this.profileId = profileId;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    public MealLog(int mealId, int profileId, Date mealDate, String mealType, int foodId, double quantity) {
        this.mealId = mealId;
        this.profileId = profileId;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    // Getters and setters
    public int getMealId() { return mealId; }
    public void setMealId(int mealId) { this.mealId = mealId; }

    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public Date getMealDate() { return mealDate; }
    public void setMealDate(Date mealDate) { this.mealDate = mealDate; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getSaturatedFat() { return saturatedFat; }
    public void setSaturatedFat(double saturatedFat) { this.saturatedFat = saturatedFat; }

    public double getTransFat() { return transFat; }
    public void setTransFat(double transFat) { this.transFat = transFat; }

    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }

    public double getFiber() { return fiber; }
    public void setFiber(double fiber) { this.fiber = fiber; }

    public double getSugars() { return sugars; }
    public void setSugars(double sugars) { this.sugars = sugars; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCholesterol() { return cholesterol; }
    public void setCholesterol(double cholesterol) { this.cholesterol = cholesterol; }

    public double getSodium() { return sodium; }
    public void setSodium(double sodium) { this.sodium = sodium; }

    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }

    public double getCalcium() { return calcium; }
    public void setCalcium(double calcium) { this.calcium = calcium; }

    public double getIron() { return iron; }
    public void setIron(double iron) { this.iron = iron; }
}
