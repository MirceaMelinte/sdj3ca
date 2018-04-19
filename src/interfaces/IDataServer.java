package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import model.Car;

public interface IDataServer extends Remote
{
   boolean executeRegisterCar(Car car) throws RemoteException, SQLException;
}
