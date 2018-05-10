package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import model.Car;
import model.CarList;
import model.Pallet;
import model.PalletList;
import model.Part;
import model.PartList;
import model.Product;
import model.ProductList;

public interface IDataServer extends Remote {
	
	// GET

	CarList executeGetAllCars() throws RemoteException, SQLException;

	PartList executeGetAllParts() throws RemoteException, SQLException;

	PalletList executeGetAllPallets() throws RemoteException, SQLException;

	ProductList executeGetAllProducts() throws RemoteException, SQLException;

	PartList executeGetPartsByProduct(int productId) throws RemoteException, SQLException;

	PartList executeGetPartsByPallet(int palletId) throws RemoteException, SQLException;

	PartList executeGetPartsByCar(String chassisNumber) throws RemoteException, SQLException;

	ProductList executeGetStolenProducts(String chassisNumber) throws RemoteException, SQLException;

	// Car executeGetStolenCar(String chassisNumber) throws RemoteException, SQLException;

	// INSERT
	
	Car executeRegisterCar(Car car) throws RemoteException, SQLException;

	Product executeRegisterProduct(Product product) throws RemoteException, SQLException;

	Part executeRegisterNewPart(Part part, String chassisNumber) throws RemoteException, SQLException;

	// UPDATE
	
	Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException;

	Pallet executeUpdatePalletState(Pallet pallet, String state) throws RemoteException, SQLException;

	Car executeUpdateCarState(Car car, String state) throws RemoteException, SQLException;

	// Observable methods
	
	void attach(IObserver observer) throws RemoteException;

	void dettach(IObserver observer) throws RemoteException;
}