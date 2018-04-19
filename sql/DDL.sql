DROP TABLE Part PURGE;
DROP TABLE Car PURGE;
DROP TABLE Pallet PURGE;
DROP TABLE Package PURGE;

-- 

CREATE TABLE Car(
    id NUMBER(6, 0) GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    manufacturer VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year NUMBER(4, 0) NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    chassisNumber VARCHAR(17) NOT NULL,
    isReady NUMBER(1, 0) NOT NULL
        CONSTRAINT coCarIsReady 
        CHECK (isReady IN ('1', '0')),
    isFinished NUMBER(1, 0) NOT NULL
        CONSTRAINT coCarIsFinished
        CHECK (isFinished IN ('1', '0'))
);

-- 

CREATE TABLE Package(
    id NUMBER(6, 0) GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL
);

--

CREATE TABLE Pallet(
    id NUMBER(6, 0) GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    partType VARCHAR(100) NOT NULL,
    maxWeight DOUBLE PRECISION NOT NULL,
    isFinished NUMBER(1, 0) NOT NULL
        CONSTRAINT coPalletIsFinished
        CHECK (isFinished IN ('1', '0'))
);

--

CREATE TABLE Part(
    id NUMBER(6, 0) GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    carId NUMBER(6, 0) NOT NULL
        REFERENCES Car (id),
    palletId NUMBER(6, 0) NOT NULL
        REFERENCES Pallet (id),
    packageId NUMBER(6, 0) NOT NULL
        REFERENCES Package (id)
);

-- ##################################
-- INSERTS AFTER TABLES WERE CREATED
-- ##################################

INSERT INTO Pallet (id, partType, maxWeight, isFinished)
    VALUES (-1, 'no pallet', 0, 0);
    
INSERT INTO Package (id, type, name)
    VALUES (-1, 'no package', 'no package');
COMMIT;        