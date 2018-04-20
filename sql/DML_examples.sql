SELECT * FROM Pallet;

DELETE FROM Part;

INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, isReady, isFinished)
    VALUES ('BMW', 'X5', 2016, 2500, '1JCCM85E5BT001312', 0, 0);
    
INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, isReady, isFinished)
    VALUES ('AUDI', 'R8', 2018, 1900, '1JDDM85EABT002212', 0, 0);
    
INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, isReady, isFinished)
    VALUES ('BMW', 'M3', 2015, 2300, '1JKTK85E3AT001312', 0, 0);
    
INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, isReady, isFinished)
    VALUES ('AUDI', 'TT', 2013, 2150, '1JKLM85E5BT231312', 0, 0);
    
    
INSERT INTO Part (name, type, weight, carId, palletId, productId)
    VALUES ('RAS823', 'STEERING WHEEL', 21.1, 1, -1, -1);
    
INSERT INTO Part (name, type, weight, carId, palletId, productId)
    VALUES ('RDS823', 'HOOD', 31.1, 1, -1, -1);

INSERT INTO Part (name, type, weight, carId, palletId, productId)
    VALUES ('RBS823', 'WINDSHIELD', 23.3, 1, -1, -1);

INSERT INTO Pallet (partType, maxWeight, isFinished)
    VALUES ('Tires', 500, 0);
    
    
    
    