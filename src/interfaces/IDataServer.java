package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import model.*;

public interface IDataServer extends Remote {
	PartCache executeGetPartCache() throws RemoteException, SQLException;
	
	CarCache executeGetCarCache() throws RemoteException, SQLException;
	
	PalletCache executeGetPalletCache() throws RemoteException, SQLException;
	
	ProductCache executeGetProductCache() throws RemoteException, SQLException;

	Car executeRegisterCar(Car car) throws RemoteException, SQLException;

	Pallet executeRegisterPallet(Pallet pallet) throws RemoteException, SQLException;

	Product executeRegisterProduct(Product product) throws RemoteException, SQLException;

	Part executeRegisterNewPart(Part part) throws RemoteException, SQLException;

	Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException;

	Part executeUpdatePartProduct(Part part, Product product) throws RemoteException, SQLException;

	Pallet executeFinishPallet(Pallet pallet) throws RemoteException, SQLException;

	Car executeFinishCar(Car car) throws RemoteException, SQLException;

	PartList executeGetStolenParts(Car car) throws RemoteException, SQLException;
}