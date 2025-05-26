# 🥦 NutriSci  
By Team Bravo | EECS 3311 — Summer 2025

NutriSci is a Java-based desktop nutrition tracking app built using Swing, MySQL, and CNF 2015 data. It allows users to create personalized profiles, run nutrition queries (like "Vitamin C in apples"), and log meals for nutrient tracking — with support for visualizations and intelligent food recommendations coming soon.

---

## ✅ Features

- 📊 Load the **CNF 2015 dataset** (13 CSVs) into MySQL automatically
- 🔍 Query nutrients in real food data (Java + SQL)
- 👤 Create and manage **user nutrition profiles**
- 🧑‍💼 Splash screen to select an existing user or create a new one
- ✏️ Edit profile attributes (height, weight, DOB, units)
- 🔗 Foundation for meal logging, charting, and swap suggestions

---

## 🧑‍💻 Developer Environment Setup

### 📦 Prerequisites

| Tool            | Version          | Notes                        |
|-----------------|------------------|------------------------------|
| JDK             | `OpenJDK 23.0.2` | Installed via Homebrew       |
| IntelliJ IDEA   | CE (2024.1+)     | Maven support included       |
| MySQL           | `brew install mysql` | Version 8+ preferred    |
| GitHub Desktop  | Optional         | For repo sync                |

---

### 🛠️ 1. Clone the Repo

```bash
cd ~/IdeaProjects
git clone https://github.com/YOUR_USERNAME/NutriSci.git
cd NutriSci
````

> Make sure `NutriSci/` is your IntelliJ project root (not nested inside another `NutriSci/` folder).

---

### 🧪 2. Start MySQL

```bash
brew services start mysql
```

Then enter MySQL:

```bash
mysql -u root
CREATE DATABASE nutriscidb;
```

---

### 🧱 3. Load CNF Table Schema

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
├── YIELD NAME.csv
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

### 🧾 SQL Table: `user_profile`

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

### 👨‍💻 Profile UI (Java Swing)

#### 1. Splash Screen

```java
ProfileSelector.java
```

* Shows dropdown of all saved profiles
* Button to create new profile via `ProfileForm`
* Proceeds to `Dashboard` on selection

#### 2. Profile Creation

```java
ProfileForm.java
```

* Enter name, DOB, height, weight, units
* On save → data is inserted into MySQL
* Redirects to `Dashboard`

#### 3. Profile Editor

```java
ProfileEditor.java
```

* Allows editing of DOB, height, and weight
* Saves changes to MySQL with update query
* Has "Back to Dashboard" button

#### 4. Dashboard

```java
Dashboard.java
```

* Displays interactive cards for features:

  * Edit Profile
  * Log Meal *(coming soon)*
  * Visualize Nutrients *(coming soon)*
  * Food Swaps *(coming soon)*


---

### 🎬 Launch the App UI

To start the full desktop UI with profile selection:

> 💡 Make sure you’ve already created the `user_profile` table and loaded CNF data using `CNFImporter`.

---

#### 🖥️ Option 1: Run from IntelliJ

1. Open `ProfileSelector.java`
2. Right-click anywhere in the file → `Run ProfileSelector.main()`

This will open the **Splash Screen**, where you can:

* Select an existing profile
* Create a new profile (via `ProfileForm`)
* Proceed to the **Dashboard**

---

#### 🖥️ Option 2: Run from Terminal

From project root (if you’ve packaged your app):

```bash
cd target
java -cp classes org.example.ui.ProfileSelector
```

Or use the full `mvn compile exec` setup if needed.

---

### 🔁 UI Navigation Flow

| Screen         | File              | Trigger                      |
| -------------- | ----------------- | ---------------------------- |
| Splash screen  | `ProfileSelector` | App launch                   |
| Create profile | `ProfileForm`     | Click “Create New Profile”   |
| Dashboard      | `Dashboard`       | After selecting a profile    |
| Edit profile   | `ProfileEditor`   | From dashboard → Edit button |

---

## 🗂️ File Structure

```
NutriSci/
├── src/
│   └── main/java/org/example/
│       ├── dao/
│       │   └── UserProfileDAO.java
│       ├── model/
│       │   └── UserProfile.java
│       ├── ui/
│       │   ├── BMRWindow.java
│       │   ├── ProfileForm.java
│       │   ├── ProfileSelector.java
│       │   ├── ProfileEditor.java
│       │   └── Dashboard.java
│       ├── CSVLoader.java
│       ├── CNFImporter.java
│       └── CNFTestQuery.java
├── create_cnf_tables_ordered.sql
├── .gitignore
├── README.md
└── data/                  # contains all CNF CSVs (ignored)
```

---

## 🔭 Coming Soon

* 🧾 Meal logging (per date/meal type)
* 🔁 Smart food swaps (goal-driven replacements)
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

* Shaf Muhammad
* Team Bravo — EECS 3311 Summer 2025
