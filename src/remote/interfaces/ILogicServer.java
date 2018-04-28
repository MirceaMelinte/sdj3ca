package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Car;
import model.Pallet;
import model.Part;
import model.PartList;
import model.Product;
import model.ProductList;
import model.cache.PartCache;

public interface ILogicServer extends Remote {

	// client 1
	String validateRegisterCar(Car car) throws RemoteException;
	// client 2
	String validateFinishDismantling(Car car) throws RemoteException; // TODO Client
	String validateFinishPallet(Pallet pallet) throws RemoteException; // TODO Client
	String validateRegisterPart(Part part) throws RemoteException;
	String validateRegisterPallet(Pallet pallet) throws RemoteException;
	String validateSetPalletWeight(Pallet pallet, double newWeight) throws RemoteException; // TODO Client

	// client 3
	String validateRegisterProduct(Product product, PartList partList) throws RemoteException;
	PartCache getParts() throws RemoteException;

	// client 4
	PartList validateGetStolenParts(Car car) throws RemoteException;
	ProductList validateGetStolenProducts(Car car) throws RemoteException;
	Car validateGetStolenCar(String chassisNumber) throws RemoteException;
}
