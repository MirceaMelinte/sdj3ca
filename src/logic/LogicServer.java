package logic;

import interfaces.IDataServer;
import interfaces.ILogicServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import model.Car;
import model.PartList;

public class LogicServer extends UnicastRemoteObject implements ILogicServer
{
   private static final long serialVersionUID = 1L;
   private IDataServer dataServer;
   
   private PartList cache;
   
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
         
         System.out.println("Logic server is running... ");
         
         this.cache = this.dataServer.executeGetParts();
         
         System.out.println((this.cache != null)
        		 				? "Parts cache is now up-to-date. "
	 							: "A problem has occured when updating the parts cache. ");
         
      } catch( Exception e ) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) throws RemoteException
   {
      LogicServer l = new LogicServer();
      l.begin();
      
//      Car c = new Car();
//      
//      c.setCarId(1);
//      
//      PartList list = l.validateGetStolenParts(c);
//      
//      for (int i = 0; i < list.count(); i++)
//      {
//         System.out.println(list.getPart(i).getName());
//      }
   }

   @Override
   public String validateRegisterCar(Car car) throws RemoteException
   {
      try
      {
         if(dataServer.executeRegisterCar(car) != null)
         {
            return "The car was registered";
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      return "The car registration has failed";
   }

   @Override
   public PartList validateGetStolenParts(Car car) throws RemoteException
   {
      try
      {
         return dataServer.executeGetStolenParts(car);
         
      }
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }
}
