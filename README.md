# 🥦 NutriSci: CNF Nutrition Database App  
By Team Bravo | EECS 3311 — Summer 2025

NutriSci is a Java-based desktop app that lets users explore nutrient data from the **Canadian Nutrient File (CNF 2015)**. You can view nutritional details, serving sizes, and eventually build personalized profiles and nutrition recommendations.

---

## ✅ Features

- Load CNF 2015 dataset (13 CSVs) into MySQL automatically
- Run nutrition queries like "Vitamin C in apples"
- Search food and nutrients using Java + SQL
- Ready for profile editing, nutrient charts (JFreeChart), and more

---

## 🧑‍💻 Developer Environment Setup

### 📦 Prerequisites

| Tool            | Version          | Notes                        |
|-----------------|------------------|------------------------------|
| macOS           | Ventura / Sonoma | Tested on M1 Mac             |
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

Then in terminal:

```bash
mysql -u root
CREATE DATABASE nutriscidb;
```

---

### 🧱 3. Load Table Schema

Run the provided ordered table script:

```bash
mysql -u root nutriscidb < create_cnf_tables_ordered.sql
```

This creates all 13 CNF tables in dependency order with foreign keys.

---

### 📥 4. Place CNF CSVs

Put all these files into a `data/` folder in your project root:

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

> ⚠️ Do not commit this folder — it's ignored by `.gitignore`.

---

### 🚀 5. Run the Loader

In IntelliJ, run:

```java
CNFImporter.java
```

This uses OpenCSV and JDBC to insert all data safely with error handling and duplicate skips.

---

### 🧪 6. Run the Test Query

To confirm data is in MySQL:

```java
CNFTestQuery.java
```

Expected output:

```
✅ Sample food_name rows:
 - 2: Cheese souffle
...

🔍 Non-zero nutrients for 'apple':
 - CARBOHYDRATE, TOTAL: 11.43 g
 - SUGARS: 9.23 g
...
```

---

## 🧾 File Structure

```
NutriSci/
├── src/main/java/org/example/
│   ├── CSVLoader.java         # Loads CSVs using OpenCSV
│   ├── CNFImporter.java       # Main entrypoint for loading
│   └── CNFTestQuery.java      # Sample queries
├── create_cnf_tables_ordered.sql
├── README.md
├── .gitignore
└── data/                      # Ignored CSV folder
```

---

## ✅ Next Steps for Team

* Build `UserProfile` model and DAO
* Create `ProfileForm.java` (Java Swing UI)
* Add splash screen to choose profile
* Connect nutrient display to user settings
* Optional: Visualize nutrient data (JFreeChart)

---

## 📚 References

* Canadian Nutrient File 2015
  [https://food-nutrition.canada.ca/cnf-fce/](https://food-nutrition.canada.ca/cnf-fce/)

* Health Canada Open Data License
  [https://open.canada.ca/en/open-government-licence-canada](https://open.canada.ca/en/open-government-licence-canada)

---

## 👩‍💻 Contributors

* Shaf Muhammad
* Team Bravo — EECS 3311

