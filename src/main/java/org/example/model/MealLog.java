package org.example.model;

import org.jfree.data.general.DefaultPieDataset;

import java.sql.Date;

public class MealLog extends HealthLog{
    private int foodId;
    private String foodName;

    // Nutrition field
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
    public MealLog() { super(); }

    public MealLog(int profileId, Date mealDate, String mealType, int foodId, double quantity) {
        super(profileId, mealDate, mealType, quantity);
        this.foodId = foodId;
    }

    public MealLog(int mealId, int profileId, Date mealDate, String mealType, int foodId, double quantity) {
        super(mealId, profileId, mealDate, mealType, quantity);
        this.foodId = foodId;
    }

    // Getters and setters

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

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

    public String getDetails() {
        return String.format("""
            Qty: %.2fg
            Calories: %.2f kcal | Protein: %.2f g | Carbs: %.2f g | Fat: %.2f g
            Sat Fat: %.2f g | Trans Fat: %.2f g | Sugars: %.2f g | Fiber: %.2f g
            Cholesterol: %.2f mg | Sodium: %.2f mg | Potassium: %.2f mg
            Calcium: %.2f mg | Iron: %.2f mg
            """,
                this.getQuantity(),
                this.getCalories(),
                this.getProtein(),
                this.getCarbohydrates(),
                this.getFat(),
                this.getSaturatedFat(),
                this.getTransFat(),
                this.getSugars(),
                this.getFiber(),
                this.getCholesterol(),
                this.getSodium()/1000,
                this.getPotassium()/1000,
                this.getCalcium()/1000,
                this.getIron()/1000
        );
    }

    public DefaultPieDataset getNutrientPieData() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        double total = protein + carbohydrates + fat + fiber + cholesterol + sodium;

        if (total > 0) {
            dataset.setValue("Protein", protein);
            dataset.setValue("Carbohydrates", carbohydrates);
            dataset.setValue("Fat", fat);
            dataset.setValue("Fiber", fiber);
            dataset.setValue("Cholesterol", cholesterol/1000);
            dataset.setValue("Sodium", sodium/1000);
        } else {
            dataset.setValue("No Data", 1);
        }

        return dataset;
    }
}
