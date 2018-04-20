package client;

import interfaces.ILogicServer;

import java.rmi.Naming;
import java.rmi.RemoteException;

import model.Car;

public class CarClient
{
   private ILogicServer logicServer;
   
   public CarClient() throws RemoteException
   {
      super();
   }
   
   public void begin()
   {
      try {
         String ip = "localhost";
         String URL = "rmi://" + ip + "/" + "logicServer";
         
         logicServer = (ILogicServer) Naming.lookup( URL );
         
         System.out.println("Car Client connection established");
      } catch( Exception ex ) {
         ex.printStackTrace();
      }
   }
   
   public void registerCar(Car car) throws RemoteException
   {
      System.out.println(logicServer.validateRegisterCar(car));
   }
   
   public static void main(String[] args) throws RemoteException
   {
      CarClient client = new CarClient();
      
      client.begin();
      
      Car car = new Car();
      
      car.setCarId(1);
      car.setManufacturer("BMW");
      car.setModel("M3");
      
      client.registerCar(car);
   }
}
