# 🥦 NutriSci

By Team Bravo | EECS 3311 — Summer 2025

NutriSci is a Java-based desktop nutrition tracking app built using Swing, MySQL, and CNF 2015 data. It allows users to create personalized profiles, log meals, analyze nutrient intake (like calories, carbs, iron, etc.), and visualize meal information — with support for smart food swaps and charts coming soon.

---

## ✅ Features

* 📊 Load the **CNF 2015 dataset** (13 CSVs) into MySQL automatically
* 🔍 Query real food nutrients and log meals with nutrient summaries
* 👤 Create and manage **user nutrition profiles**
* 🧑 Splash screen to select an existing user or create a new one
* ✏️ Edit profile attributes (height, weight, DOB, units)
* 🍽️ **Log daily meals** and view calories, protein, fat, etc.
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

> Make sure `NutriSci/` is your IntelliJ project root (not nested inside another `NutriSci/` folder).

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

Run this in terminal:

```bash
mysql -u root nutriscidb < create_cnf_tables_ordered.sql
```

This creates all 13 CNF tables with foreign keys in dependency order.

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

This loads all CSVs into MySQL using `OpenCSV`, skipping duplicates and malformed rows with error handling.

---

### 🔍 6. Test Queries (Optional)

To confirm data loaded properly, run:

```java
CNFTestQuery.java
```

Example expected output:

```
✅ Sample food_name rows:
 - 2: Cheese souffle
...

🔍 Nutrients for 'apple':
 - CARBOHYDRATE, TOTAL: 11.43 g
 - SUGARS: 9.23 g
...
```

---

## 👤 User Profile System

Users can create, select, and update personal nutrition profiles. Each user profile contains:

* Name
* Sex (`Male`, `Female`, `Other`)
* Date of birth
* Height (cm)
* Weight (kg)
* Units (`Metric` or `Imperial`)

---

### 📟 SQL Table: `user_profile`

```sql
CREATE TABLE IF NOT EXISTS user_profile (
    ProfileID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
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

Users can log meals (breakfast, lunch, dinner, snacks) and specify food items and quantities. The app automatically calculates nutrients using CNF data.

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

### 🔍 Auto-Calculated Nutrients

From the `nutrient_amount` and `nutrient_name` tables, the following are computed per meal:

* Calories
* Protein
* Carbohydrates
* Fat
* Saturated + Trans Fat
* Sugars
* Fiber
* Sodium, Potassium
* Calcium, Iron
* Cholesterol

---

### 👨‍💼 UI Components

#### 1. `MealLogger.java`

* UI form to log meals with:

  * Date
  * Meal type
  * Food search (dropdown)
  * Quantity (grams/ml)

#### 2. `MealViewer.java`

* Shows logged meals for a user profile
* Displays meal date, food name, and full nutrient breakdown

---

### 🎮 Launch the App UI

To start the full desktop UI with profile selection:

> 💡 Make sure you’ve already created the `user_profile` and `meal_log` tables and loaded CNF data using `CNFImporter`.

#### 💻 Option 1: Run from IntelliJ

1. Open `ProfileSelector.java`
2. Right-click → `Run ProfileSelector.main()`

#### 💻 Option 2: Run from Terminal

```bash
cd target
java -cp classes org.example.ui.ProfileSelector
```

---

### ♻️ UI Navigation Flow

| Screen         | File                   | Trigger                      |
| -------------- | -----------------      | ---------------------------- |
| Splash screen  | `ProfileSelector`      | App launch                   |
| Create profile | `ProfileForm`          | Click “Create New Profile”   |
| Dashboard      | `Dashboard`            | After selecting a profile    |
| Edit profile   | `ProfileEditor`        | From dashboard → Edit        |
| Log Meal       | `MealLogger`           | From dashboard → Log button  |
| View Meals     | `MealViewer`           | From dashboard               |
| View BMR       | `BMRWindow`            | From dashboard               |
| View Macros    | `NutrientChartWindow`  | From dashboard               |

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
