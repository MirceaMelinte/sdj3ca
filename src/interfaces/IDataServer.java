package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import model.Car;
import model.PartList;

public interface IDataServer extends Remote
{
   boolean executeRegisterCar(Car car) throws RemoteException, SQLException;
   PartList executeGetStolenParts(Car car) throws RemoteException, SQLException;
}
