package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Car;
import model.PartList;

public interface ILogicServer extends Remote
{
   String validateRegisterCar(Car car) throws RemoteException;
   PartList validateGetStolenParts(Car car) throws RemoteException;
}
