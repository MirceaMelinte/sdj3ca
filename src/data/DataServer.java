package data;

import interfaces.IDataServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Car;
import model.Pallet;
import model.Part;
import model.PartList;
import model.Product;

public class DataServer extends UnicastRemoteObject implements IDataServer
{
   private static final long serialVersionUID = 1L;
   private static Connection connection;

   public DataServer() throws RemoteException 
   {
      super();
      
      DataServer.connection = PersistenceConfig.establishConnection(connection);
   }

   public void begin()
   {
      try {
         LocateRegistry.createRegistry(1099);
         
         Naming.rebind("dataServer", this);
         
         System.out.println("Data server is running... ");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public boolean executeRegisterCar(Car car)
   {
      return !car.equals(null);
   }

   @Override
   public PartList executeGetStolenParts(Car car) throws RemoteException,
         SQLException
   {
      try 
      {
         PreparedStatement statement = 
               DataServer.connection.prepareStatement("SELECT * FROM Part "
                                             + "WHERE  = carId = ?");
         
         statement.setDouble(1, car.getCarId());
         ResultSet rs = statement.executeQuery();
         PartList partList = new PartList();
         
         while(rs.next())
         {
            Part part = new Part();
            part.setPartId(rs.getInt("id"));
            part.setName(rs.getString("name"));
            part.setType(rs.getString("type"));
            part.setWeight(rs.getDouble("weight"));
            part.setCar(car);
            Product product = new Product();
            product.setProductId(rs.getInt("productId"));
            Pallet pallet = new Pallet();
            pallet.setPalletId(rs.getInt("palletId"));
            partList.addPart(part);
         }

         statement.close();
         rs.close();
         
         System.out.println("[SUCCESS] Part List Retrieved");
         
         return partList;
      }
      catch (SQLException e) {
         DataServer.connection.rollback();
         System.out.println("[FAIL] Part List Retrieval Failed");
         e.printStackTrace();
      }
      return null;
   }
   
   public static void main(String[] args) throws RemoteException
   {
      DataServer d = new DataServer();
      
      d.begin();
   }
}
