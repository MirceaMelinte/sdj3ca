------------------------
-- DATA EXAMPLES -------

SELECT * FROM Pallet;

INSERT INTO Car (manufacturer, model, year, weight, chassisNumber, isReady, isFinished)
    VALUES ('BMW', 'X5', 2016, 2500, '1JCCM85E5BT001312', 0, 0);
    
INSERT INTO Pallet (partType, maxWeight, isFinished)
    VALUES ('Tires', 500, 0);