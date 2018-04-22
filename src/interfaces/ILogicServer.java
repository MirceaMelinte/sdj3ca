package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Car;
import model.Pallet;
import model.Part;
import model.PartList;
import model.Product;

public interface ILogicServer extends Remote {

	// client 1
	String validateRegisterCar(Car car) throws RemoteException;

	// client 2
	String validateRegisterPart(Part part) throws RemoteException;

	String validateRegisterPallet(Pallet pallet) throws RemoteException;

	String validateFinishPallet(Pallet pallet) throws RemoteException;

	// client 3
	String validateRegisterProduct(Product product, PartList partList) throws RemoteException;

	PartList getParts() throws RemoteException;

	// client 4
	PartList validateGetStolenParts(Car car) throws RemoteException;
}
