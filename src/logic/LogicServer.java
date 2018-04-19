package logic;

import interfaces.ILogicServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import model.Car;
import interfaces.IDataServer;

public class LogicServer extends UnicastRemoteObject implements ILogicServer
{
   private static final long serialVersionUID = 1L;
   private IDataServer dataServer;
   
   public LogicServer() throws RemoteException
   {
      super();
   }
   
   public void begin() throws RemoteException
   {
      try {       
         LocateRegistry.getRegistry(1099); // Registry is created on dataServer, here we just get it
         
         Naming.rebind("logicServer", this);
         
         String ip = "localhost";
         String URL = "rmi://" + ip + "/" + "dataServer";
         
         dataServer = (IDataServer) Naming.lookup( URL );
         
         System.out.println("Logic server is running");
      } catch( Exception e ) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) throws RemoteException
   {
      LogicServer l = new LogicServer();
      l.begin();
   }

   @Override
   public String validateRegisterCar(Car car) throws RemoteException
   {
      if(dataServer.executeRegisterCar(car))
      {
         return "The car was registered";
      }
      return "The car registration has failed";
   }
}
