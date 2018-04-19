package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Car;

public interface ILogicServer extends Remote
{
   String validateRegisterCar(Car car) throws RemoteException;
}
