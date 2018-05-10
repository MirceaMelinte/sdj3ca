package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.*;

public interface ILogicServer extends Remote {

	// client 1

	String validateRegisterCar(Car car) throws RemoteException;

	// client 2

	CarList getAvailableCars() throws RemoteException;

	String validateRegisterPart(Part part, String chassisNumber)
			throws RemoteException;

	String validatePutPart(int partId, int palletId) throws RemoteException;

	String validateFinishPallet(int palletId) throws RemoteException;

	String validateFinishDismantling(String chassisNumber)
			throws RemoteException;

	Pallet findAvailablePallet(Part part) throws RemoteException;

	// client 3

	String validateRegisterProduct(Product product) throws RemoteException;

	// client 4

	PartList validateGetPartsByCar(String chassisNumber)
			throws RemoteException;

	ProductList validateProductsByCar(String chassisNumber)
			throws RemoteException;

	Car validateGetCar(String chassisNumber) throws RemoteException;
}