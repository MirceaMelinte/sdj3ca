SELECT * FROM Part;
SELECT * FROM Car;

DELETE FROM Part;
DELETE FROM Pallet;
DELETE FROM Car;
DELETE FROM Product;

INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, state)
    VALUES ('BMW', 'X5', 2016, 2500, '1JCCM85E5BT001312', 'Finished');
    
INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, state)
    VALUES ('AUDI', 'R8', 2018, 1900, '1JDDM85EABT002212', 'Finished');
    
INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, state)
    VALUES ('BMW', 'M3', 2015, 2300, '1JKTK85E3AT001312', 'Finished');
    
INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, state)
    VALUES ('AUDI', 'TT', 2013, 2150, '1JKLM85E5BT231312', 'Finished');
    
    
INSERT INTO Part (type, weight, carId, palletId, productId)
    VALUES ('STEERING WHEEL', 21.1, 1, -1, -1);
    
INSERT INTO Part (type, weight, carId, palletId, productId)
    VALUES ('HOOD', 31.1, 1, -1, -1);

INSERT INTO Part (type, weight, carId, palletId, productId)
    VALUES ('WINDSHIELD', 23.3, 1, -1, -1);

INSERT INTO Pallet (partType, weight, maxWeight, state)
    VALUES ('Tires', 0, 500, 'Available');
      
commit;
    