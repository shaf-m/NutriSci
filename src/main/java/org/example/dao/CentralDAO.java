package org.example.dao;

public class CentralDAO {
    private static CentralDAO instance;
    private ExerciseLogDAO exerciseLogDAO;
    private FoodSearchDAO foodSearchDAO;
    private MealLogDAO mealLogDAO;
    private UserProfileDAO userProfileDAO;

    private CentralDAO() {
        exerciseLogDAO = new ExerciseLogDAO();
        foodSearchDAO = new FoodSearchDAO();
        mealLogDAO = new MealLogDAO();
        userProfileDAO = new UserProfileDAO();
    }

    public static synchronized CentralDAO getInstance() {
        if (instance == null) {
            instance = new CentralDAO();
        }
        return instance;
    }

    // Getters for the DAOs
    public ExerciseLogDAO getExerciseLogDAO() {
        return exerciseLogDAO;
    }

    public FoodSearchDAO getFoodSearchDAO() {
        return foodSearchDAO;
    }

    public MealLogDAO getMealLogDAO() {
        return mealLogDAO;
    }

    public UserProfileDAO getUserProfileDAO() {
        return userProfileDAO;
    }
}
