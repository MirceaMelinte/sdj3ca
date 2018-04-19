package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Car;

public interface IDataServer extends Remote
{
   boolean executeRegisterCar(Car car) throws RemoteException;
}
