package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.*;
import model.cache.*;

public interface ILogicServer extends Remote {

	// client 1
	
	String validateRegisterCar(Car car) throws RemoteException;

	
	// client 2
	
	CarList getAvailableCars() throws RemoteException;

	String validateRegisterPart(Part part) throws RemoteException;

	String validatePutPart(int partId, int palletId) throws RemoteException;

	String validateFinishPallet(int palletId) throws RemoteException;

	String validateFinishDismantling(String chassisNumber) throws RemoteException;


	// client 3
	
	String validateRegisterProduct(Product product, PartList partList) throws RemoteException;

	PartCache getParts() throws RemoteException;

	
	// client 4
	
	PartList validateGetStolenParts(Car car) throws RemoteException;

	ProductList validateGetStolenProducts(Car car) throws RemoteException;

	Car validateGetStolenCar(String chassisNumber) throws RemoteException;
}
