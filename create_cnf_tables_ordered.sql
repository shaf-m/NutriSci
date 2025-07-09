
CREATE TABLE food_group (
    FoodGroupID INT PRIMARY KEY,
    FoodGroupCode INT,
    FoodGroupName VARCHAR(200),
    FoodGroupNameF VARCHAR(200)
);


CREATE TABLE food_source (
    FoodSourceID INT PRIMARY KEY,
    FoodSourceCode INT,
    FoodSourceDescription VARCHAR(200),
    FoodSourceDescriptionF VARCHAR(200)
);


CREATE TABLE nutrient_name (
    NutrientNameID INT PRIMARY KEY,
    NutrientCode INT,
    NutrientSymbol VARCHAR(10),
    Unit VARCHAR(8), --NutrientUnit
    NutrientName VARCHAR(200),
    NutrientNameF VARCHAR(200),
    Tagname VARCHAR(20),
    NutrientDecimals INT
);


CREATE TABLE nutrient_source (
    NutrientSourceID INT PRIMARY KEY,
    NutrientSourceCode INT,
    NutrientSourceDescription VARCHAR(200),
    NutrientSourceDescriptionF VARCHAR(200)
);


CREATE TABLE measure_name (
    MeasureID INT PRIMARY KEY,
    MeasureName VARCHAR(200), --MeasureDescription
    MeasureNameF VARCHAR(200) --MeasureDescriptionF
);


CREATE TABLE refuse_name (
    RefuseID INT PRIMARY KEY,
    RefuseName VARCHAR(200),--RefuseDescription
    RefuseNameF VARCHAR(200) --RefuseDescriptionF
);


CREATE TABLE yield_name (
    YieldID INT PRIMARY KEY,
    YieldName VARCHAR(200), --YieldDescription
    YieldNameF VARCHAR(200) --YieldDescriptionF
);


CREATE TABLE food_name (
    FoodID INT PRIMARY KEY,
    FoodCode INT,
    FoodGroupID INT,
    FoodSourceID INT,
    FoodDescription VARCHAR(255),
    FoodDescriptionF VARCHAR(255),
    CountryCode INT,
    FoodDateOfEntry DATE,
    FoodDateOfPublication DATE,
    ScientificName VARCHAR(100)
);


CREATE TABLE conversion_factor (
    FoodID INT,
    MeasureID INT,
    ConversionFactorValue DECIMAL(10,5),
    ConvFactorDateOfEntry DATE,
    PRIMARY KEY (FoodID, MeasureID),
    FOREIGN KEY (FoodID) REFERENCES food_name(FoodID),
    FOREIGN KEY (MeasureID) REFERENCES measure_name(MeasureID)
);


CREATE TABLE refuse_amount (
    FoodID INT,
    RefuseID INT,
    RefuseAmount DECIMAL(9,5),
    RefuseDateOfEntry DATE,
    PRIMARY KEY (FoodID, RefuseID),
    FOREIGN KEY (FoodID) REFERENCES food_name(FoodID),
    FOREIGN KEY (RefuseID) REFERENCES refuse_name(RefuseID)
);


CREATE TABLE yield_amount (
    FoodID INT,
    YieldID INT,
    YieldAmount DECIMAL(9,5),
    YieldDateOfEntry DATE,
    PRIMARY KEY (FoodID, YieldID),
    FOREIGN KEY (FoodID) REFERENCES food_name(FoodID),
    FOREIGN KEY (YieldID) REFERENCES yield_name(YieldID)
);


CREATE TABLE nutrient_amount (
    FoodID INT,
    NutrientNameID INT,
    NutrientSourceID INT,
    NutrientValue DECIMAL(12,5),
    StandardError DECIMAL(8,4),
    NumberOfObservations INT,
    NutrientDateEntry DATE,
    PRIMARY KEY (FoodID, NutrientNameID),
    FOREIGN KEY (FoodID) REFERENCES food_name(FoodID),
    FOREIGN KEY (NutrientNameID) REFERENCES nutrient_name(NutrientNameID),
    FOREIGN KEY (NutrientSourceID) REFERENCES nutrient_source(NutrientSourceID)
);
