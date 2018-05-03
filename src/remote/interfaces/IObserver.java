package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Transaction;

public interface IObserver extends Remote
{
   <T> void update(Transaction<T> t) throws RemoteException;
}
