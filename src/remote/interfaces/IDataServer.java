package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import model.*;

public interface IDataServer extends Remote {
   
	Car executeGetCarByChassisNumber(String chassisNumber) throws RemoteException;
	PalletList executeGetAvailablePallets(Part part) throws RemoteException;
	CarList executeGetAvailableCars() throws RemoteException;
	Pallet executeGetPalletById(int palletId) throws RemoteException;
	Part executeGetPartById(int partId) throws RemoteException;
	Car executeGetCarByPart(int partId) throws RemoteException;
	PartList executeGetPartsByProduct(int productId) throws RemoteException;
	PartList executeGetStolenParts(String chassisNumber) throws RemoteException, SQLException;
	ProductList executeGetStolenProducts(String chassisNumber) throws RemoteException, SQLException;
	Car executeGetStolenCar(String chassisNumber) throws RemoteException, SQLException;
	
	// REGISTER
	Car executeRegisterCar(Car car) throws RemoteException, SQLException;
	Pallet executeRegisterPallet(Pallet pallet) throws RemoteException, SQLException;
	Product executeRegisterProduct(Product product) throws RemoteException, SQLException;
	Car executeRegisterNewPart(Part part, String chassisNumber) throws RemoteException, SQLException;

	// UPDATE
	Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException;
	Part executeUpdatePartProduct(Part part, Product product) throws RemoteException, SQLException;
	Pallet executeUpdatePalletWeight(Pallet pallet) throws RemoteException, SQLException;
	Pallet executeUpdatePalletState(Pallet pallet, String state) throws RemoteException, SQLException;
	Car executeUpdateCarState(Car car, String state) throws RemoteException, SQLException;
	
	// DELETE
	// Maybe few methods for deleting items from database and making sure all caches are updated
	
	// Observable methods
	void attatch(IObserver observer) throws RemoteException;
	void dettach(IObserver observer) throws RemoteException;
}