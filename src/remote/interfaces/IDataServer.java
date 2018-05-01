package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import model.*;

public interface IDataServer extends Remote {
	Pallet getPalletById(int palletId) throws RemoteException;
	
	Part getPartById(int partId) throws RemoteException;
	
	Car getCarByPart(int partId) throws RemoteException;
	
	PartList getPartsByProduct(int productId) throws RemoteException;
	
	Car executeRegisterCar(Car car) throws RemoteException, SQLException;

	Pallet executeRegisterPallet(Pallet pallet) throws RemoteException, SQLException;

	Product executeRegisterProduct(Product product) throws RemoteException, SQLException;

	Part executeRegisterNewPart(Part part) throws RemoteException, SQLException;

	Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException;

	Part executeUpdatePartProduct(Part part, Product product) throws RemoteException, SQLException;
	
	Pallet executeUpdatePalletWeight(Pallet pallet) throws RemoteException, SQLException;

	Pallet executeSetPalletState(Pallet pallet, String state) throws RemoteException, SQLException;

	Car executeSetCarState(Car car, String state) throws RemoteException, SQLException;
}