package data;

import interfaces.IDataServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import model.Car;

public class DataServer extends UnicastRemoteObject implements IDataServer
{
   private static final long serialVersionUID = 1L;

   public DataServer() throws RemoteException 
   {
      super();
   }

   public void begin()
   {
      try {
         LocateRegistry.createRegistry(1099);
         
         Naming.rebind("dataServer", this);
         
         System.out.println("Data server is running");
      } catch( Exception e ) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) throws RemoteException
   {
      DataServer d = new DataServer();
      
      d.begin();
   }

   @Override
   public boolean executeRegisterCar(Car car)
   {
      return !car.equals(null);
   }
}
