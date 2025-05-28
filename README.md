# 🥦 NutriSci

By Team Bravo | EECS 3311 — Summer 2025

NutriSci is a Java-based desktop nutrition tracking app built using Swing, MySQL, and CNF 2015 data. It allows users to create personalized profiles, log meals, analyze nutrient intake (like calories, carbs, iron, etc.), and visualize meal information — with support for smart food swaps, charts, and login/authentication added in this release.

---

## ✅ Features

* 🔐 Login & signup system with hashed credentials (or plain for dev testing)
* 👤 Create and manage user nutrition profiles
* 📊 Load the **CNF 2015 dataset** (13 CSVs) into MySQL automatically
* 🔍 Query real food nutrients and log meals with nutrient summaries
* 🧑 Splash screen to select or create a user profile (discontinued)
* ✏️ Edit profile attributes (height, weight, DOB, units)
* 🍽️ Log daily meals and view calories, protein, fat, etc.
* 📜 Meal history viewer with full nutrition breakdowns

---

## 🧑‍💻 Developer Environment Setup

### 📦 Prerequisites

| Tool           | Version              | Notes                  |
| -------------- | -------------------- | ---------------------- |
| JDK            | `OpenJDK 23.0.2`     | Installed via Homebrew |
| IntelliJ IDEA  | CE (2024.1+)         | Maven support included |
| MySQL          | `brew install mysql` | Version 8+ preferred   |
| GitHub Desktop | Optional             | For repo sync          |

---

### 🛠️ 1. Clone the Repo

```bash
cd ~/IdeaProjects
git clone https://github.com/YOUR_USERNAME/NutriSci.git
cd NutriSci
```

> Ensure `NutriSci/` is your IntelliJ project root (not nested inside another `NutriSci/` folder).

---

### 🥪 2. Start MySQL

```bash
brew services start mysql
```

Then enter MySQL:

```bash
mysql -u root
CREATE DATABASE nutriscidb;
```

---

### ⚙️ 3. Load CNF Table Schema

```bash
mysql -u root nutriscidb < create_cnf_tables_ordered.sql
```

---

### 📥 4. Place CNF CSVs

Put all these into `data/` at the project root:

```
data/
├── CONVERSION FACTOR.csv
├── FOOD GROUP.csv
├── FOOD NAME.csv
├── FOOD SOURCE.csv
├── MEASURE NAME.csv
├── NUTRIENT AMOUNT.csv
├── NUTRIENT NAME.csv
├── NUTRIENT SOURCE.csv
├── REFUSE AMOUNT.csv
├── REFUSE NAME.csv
├── YIELD AMOUNT.csv
└── YIELD NAME.csv
```

> ⚠️ `data/` is ignored by `.gitignore`

---

### 🚀 5. Load Data with Java

In IntelliJ, run:

```java
CNFImporter.java
```

---

### 🔍 6. Test Queries (Optional)

```java
CNFTestQuery.java
```

---

## 👤 User Profile & Authentication System

### 👥 Login & Signup

* Users can register with a unique username and password
* Passwords can be stored as SHA-256 hashed or plain (for testing)
* After signup → user completes their profile → redirected to dashboard

### 📟 SQL Table: `user_profile`

```sql
CREATE TABLE IF NOT EXISTS user_profile (
    ProfileID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Username VARCHAR(50) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Sex ENUM('Male', 'Female', 'Other') NOT NULL,
    DateOfBirth DATE NOT NULL,
    Height_cm DECIMAL(5,2) NOT NULL,
    Weight_kg DECIMAL(5,2) NOT NULL,
    Units ENUM('Metric', 'Imperial') NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🍽️ Meal Logging

Users can log meals (breakfast, lunch, dinner, snacks) and specify food items and quantities. Nutrients are auto-calculated.

### 📋 SQL Table: `meal_log`

```sql
CREATE TABLE meal_log (
    MealID INT AUTO_INCREMENT PRIMARY KEY,
    ProfileID INT,
    MealDate DATE,
    MealType VARCHAR(20),
    FoodID INT,
    Quantity DECIMAL(6,2),
    FOREIGN KEY (ProfileID) REFERENCES user_profile(ProfileID),
    FOREIGN KEY (FoodID) REFERENCES food_name(FoodID)
);
```

---

### 👨‍💼 UI Components

| Screen         | File                  | Description                          |
| -------------- | --------------------- | ------------------------------------ |
| Login          | `LoginPage.java`      | Tabbed login/signup screen           |
| Splash screen  | `ProfileSelector`     | Choose or create profile             |
| Create profile | `ProfileForm`         | Input user attributes                |
| Dashboard      | `Dashboard`           | Core menu with navigation cards      |
| Edit profile   | `ProfileEditor`       | Update user info                     |
| Log Meal       | `MealLogger`          | Add a meal with food + quantity      |
| View Meals     | `MealViewer`          | Table of all meals logged            |
| View BMR       | `BMRWindow`           | Calculate Basal Metabolic Rate       |
| View Macros    | `NutrientChartWindow` | Macronutrient distribution pie chart |

---

## 📂 File Structure

```
NutriSci/
├── src/
│   └── main/java/org/example/
│       ├── dao/
│       │   ├── UserProfileDAO.java
│       │   ├── FoodSearchDAO.java
│       │   └── MealLogDAO.java
│       ├── model/
│       │   ├── UserProfile.java
│       │   └── MealLog.java
│       ├── ui/
│       │   ├── LoginPage.java
│       │   ├── ProfileForm.java
│       │   ├── ProfileSelector.java
│       │   ├── ProfileEditor.java
│       │   ├── Dashboard.java
│       │   ├── MealLogger.java
│       │   ├── MealViewer.java
│       │   ├── NutrientChartPanel.java
│       │   ├── NutrientChartWindow.java
│       │   └── BMRWindow.java
│       ├── CSVLoader.java
│       ├── CNFImporter.java
│       └── CNFTestQuery.java
├── create_cnf_tables_ordered.sql
├── .gitignore
├── README.md
└── data/                  # contains all CNF CSVs (ignored by Git)
```

---

## 🔭 Coming Soon

* ♻️ Smart food swaps (goal-driven replacements)
* 📈 Nutrient comparisons (before/after swap)
* 📊 Charts using JFreeChart
* 🥗 Canada Food Guide alignment visualization

---

## 📚 References

* **Canadian Nutrient File 2015**
  [https://food-nutrition.canada.ca/cnf-fce/](https://food-nutrition.canada.ca/cnf-fce/)

* **Open Government License (Canada)**
  [https://open.canada.ca/en/open-government-licence-canada](https://open.canada.ca/en/open-government-licence-canada)

---

## 👩‍💻 Contributors

Team Bravo — EECS 3311 Summer 2025

* Shaf Muhammad
* Abdul Wasay
* Ariel Lubovich
* Cyrus Hui
