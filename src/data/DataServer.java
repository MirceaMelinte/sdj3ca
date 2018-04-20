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
   public PartList executeGetParts() throws RemoteException, SQLException {
	   try {
		   PreparedStatement selectStatement = DataServer.connection.prepareStatement(
				   "SELECT id, name, type, weight, carId, palletId, productId FROM part");
		   
		   ResultSet resultSet = selectStatement.executeQuery();
		   PartList parts = new PartList();
		   
		   while (resultSet.next()) {
			   Part part = new Part();
			   part.setPartId(resultSet.getInt("id"));
			   part.setName(resultSet.getString("name"));
			   part.setType(resultSet.getString("type"));
			   part.setWeight(resultSet.getDouble("weight"));
			   
			   PreparedStatement carSelectStatement = DataServer.connection.prepareStatement(
					   "SELECT model, manufacturer, year, weight, chassisNumber, isReady, isFinished "
					   + "FROM car WHERE id = ?");
			   carSelectStatement.setInt(1, resultSet.getInt("carId"));
			   ResultSet carResultSet = carSelectStatement.executeQuery();
			   while (carResultSet.next()) {
				   Car car = new Car();
				   car.setCarId(resultSet.getInt("carId"));
				   car.setModel(carResultSet.getString("model"));
				   car.setManufacturer(carResultSet.getString("manufacturer"));
				   car.setYear(carResultSet.getInt("year"));
				   car.setWeight(carResultSet.getDouble("weight"));
				   car.setChassisNumber(carResultSet.getString("chassisNumber"));
				   car.setReady(carResultSet.getBoolean("isReady"));
				   car.setFinished(carResultSet.getBoolean("isFinished"));
				   part.setCar(car);
			   }
			   carSelectStatement.close();
			   carResultSet.close();
			   
			   PreparedStatement palletSelectStatement = DataServer.connection.prepareStatement(
					   "SELECT partType, maxWeight, isFinished FROM pallet WHERE id = ?");
			   palletSelectStatement.setInt(1, resultSet.getInt("palletId"));
			   ResultSet palletResultSet = palletSelectStatement.executeQuery();
			   while (palletResultSet.next()) {
				   Pallet pallet = new Pallet();
				   pallet.setPalletId(resultSet.getInt("palletId"));
				   pallet.setPartType(palletResultSet.getString("partType"));
				   pallet.setMaxWeight(palletResultSet.getDouble("maxWeight"));
				   pallet.setFinished(palletResultSet.getBoolean("isFinished"));
				   part.setPallet(pallet);
			   }
			   palletSelectStatement.close();
			   palletResultSet.close();
			   
			   PreparedStatement productSelectStatement = DataServer.connection.prepareStatement(
					   "SELECT type, name FROM product WHERE id = ?");
			   productSelectStatement.setInt(1, resultSet.getInt("productId"));
			   ResultSet productResultSet = productSelectStatement.executeQuery();
			   while (productResultSet.next()) {
				   Product product = new Product();
				   product.setProductId(resultSet.getInt("productId"));
				   product.setType(productResultSet.getString("type"));
				   product.setName(productResultSet.getString("name"));
				   part.setProduct(product);
			   }
			   productSelectStatement.close();
			   productResultSet.close();
			   
			   parts.addPart(part);
		   }
		   
		   selectStatement.close();
		   resultSet.close();
		   System.out.println("[SUCCESS] Successful retrieval of all the parts in the database. ");
		   
		   return parts;
	   }
	   catch (Exception e) {
		   System.out.println("[FAIL] Failed execution of parts retrieval. ");
		   e.printStackTrace();
	   }
	   
	   return null;
   }

   @Override
   public Car executeRegisterCar(Car car) throws SQLException, RemoteException {
	   try {
			PreparedStatement insertStatement = DataServer.connection.prepareStatement(
					"INSERT INTO car (manufacturer, model, year, weight, chassisNumber, isReady, isFinished) "
					+ "VALUES (?, ?, ?, ?, ?, ?, 0)");
			
			insertStatement.setString(1, car.getManufacturer());
			insertStatement.setString(2, car.getModel());
			insertStatement.setInt(3, car.getYear());
			insertStatement.setDouble(4, car.getWeight());
			insertStatement.setString(5, car.getChassisNumber());
			insertStatement.setBoolean(6, car.isReady());
			insertStatement.execute();
			insertStatement.close();

			System.out.println("[SUCCESS] Successful execution of new car registration. Chassis number: " 
			      + car.getChassisNumber());
			
			PreparedStatement returnStatement = DataServer.connection.prepareStatement(
					"SELECT id FROM car WHERE chassisNumber = ?");
			
			returnStatement.setString(1, car.getChassisNumber());
			ResultSet resultSet = returnStatement.executeQuery();
			
			while (resultSet.next()) {
				car.setCarId(resultSet.getInt("id"));
			}

			returnStatement.close();
			DataServer.connection.commit();
			
			return car;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of new car registration. Chassis number: " 
					+ car.getChassisNumber());
			e.printStackTrace();
		}
	   
		return null;
   }
   
   @Override
   public Pallet executeRegisterPallet(Pallet pallet) throws RemoteException, SQLException {
	   try {
			PreparedStatement insertStatement = DataServer.connection.prepareStatement(
					"INSERT INTO pallet (partType, maxWeight, isFinished) "
					+ "VALUES (?, ?, 0)");
			
			insertStatement.setString(1, pallet.getPartType());
			insertStatement.setDouble(2, pallet.getMaxWeight());
			insertStatement.execute();
			insertStatement.close();

			PreparedStatement returnStatement = DataServer.connection.prepareStatement(
					"SELECT * FROM (SELECT id FROM pallet ORDER BY id DESC) WHERE ROWNUM = 1");
			
			ResultSet resultSet = returnStatement.executeQuery();
			
			while (resultSet.next()) {
				pallet.setPalletId(resultSet.getInt("id"));
			}

			System.out.println("[SUCCESS] Successful execution of new pallet registration. Pallet number: " 
			      + pallet.getPalletId());
			
			returnStatement.close();
			DataServer.connection.commit();
			
			return pallet;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of new pallet registration. Pallet number: " 
					+ pallet.getPalletId());
			e.printStackTrace();
		}
	   
		return null;
   }
   
   @Override
   public Product executeRegisterProduct(Product product) throws RemoteException, SQLException {
	   try {
			PreparedStatement insertStatement = DataServer.connection.prepareStatement(
					"INSERT INTO product (type, name) VALUES (?, ?)");
			
			insertStatement.setString(1, product.getType());
			insertStatement.setString(2, product.getName());
			insertStatement.execute();
			insertStatement.close();

			PreparedStatement returnStatement = DataServer.connection.prepareStatement(
					"SELECT * FROM (SELECT id FROM product ORDER BY id DESC) WHERE ROWNUM = 1");
			
			ResultSet resultSet = returnStatement.executeQuery();
			
			while (resultSet.next()) {
				product.setProductId(resultSet.getInt("id"));
			}

			System.out.println("[SUCCESS] Successful execution of new product registration. Product number: " 
			      + product.getProductId());
			
			returnStatement.close();
			DataServer.connection.commit();
			
			return product;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of new product registration. Product number: " 
					+ product.getProductId());
			e.printStackTrace();
		}
	   
		return null;
   }
   
   @Override
   public Part executeRegisterNewPart(Part part) throws RemoteException, SQLException {
	   try {
		   	DataServer.addNonExistentEntities();
		   
			PreparedStatement insertStatement = DataServer.connection.prepareStatement(
					"INSERT INTO part (name, type, weight, carId, palletId, productId) VALUES (?, ?)");
			
			insertStatement.setString(1, part.getName());
			insertStatement.setString(2, part.getType());
			insertStatement.setDouble(3, part.getWeight());
			insertStatement.setInt(4, part.getCar().getCarId());
			insertStatement.setInt(5, part.getPallet().getPalletId());
			insertStatement.setInt(6, part.getProduct().getProductId());
			insertStatement.execute();
			insertStatement.close();

			PreparedStatement returnStatement = DataServer.connection.prepareStatement(
					"SELECT * FROM (SELECT id FROM part ORDER BY id DESC) WHERE ROWNUM = 1");
			
			ResultSet resultSet = returnStatement.executeQuery();
			
			while (resultSet.next()) {
				part.setPartId(resultSet.getInt("id"));
			}

			System.out.println("[SUCCESS] Successful execution of new part registration. Part number: " 
			      + part.getPartId());
			
			returnStatement.close();
			DataServer.connection.commit();
			
			return part;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of new part registration. Part number: " 
					+ part.getPartId());
			e.printStackTrace();
		}
	   
		return null;
   }
   
   @Override
   public Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException {
	   try {
		   	DataServer.addNonExistentEntities();
		   
			PreparedStatement updateStatement = DataServer.connection.prepareStatement(
					"UPDATE part SET palletId = ? WHERE id = ?");
			
			updateStatement.setInt(1, pallet.getPalletId());
			updateStatement.setInt(2, part.getPartId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of part pallet setting. Part number: " 
			      + part.getPartId());
			
			updateStatement.close();
			DataServer.connection.commit();
			
			part.setPallet(pallet);
			
			return part;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of part pallet setting. Part number: " 
					+ part.getPartId());
			e.printStackTrace();
		}
	   
	   	return null;
   }
   
   @Override
   public Part executeUpdatePartProduct(Part part, Product product) throws RemoteException, SQLException {
	   try {
		   	DataServer.addNonExistentEntities();
		   
			PreparedStatement updateStatement = DataServer.connection.prepareStatement(
					"UPDATE part SET productId = ? WHERE id = ?");
			
			updateStatement.setInt(1, product.getProductId());
			updateStatement.setInt(2, part.getPartId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of part product setting. Part number: " 
			      + part.getPartId());
			
			updateStatement.close();
			DataServer.connection.commit();
			
			part.setProduct(product);
			
			return part;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of part product setting. Part number: " 
					+ part.getPartId());
			e.printStackTrace();
		}
	   
	   	return null;
   }
   
   @Override
   public Pallet executeFinishPallet(Pallet pallet) throws RemoteException, SQLException {
	   try {
			PreparedStatement updateStatement = DataServer.connection.prepareStatement(
					"UPDATE pallet SET isFinished = 1 WHERE id = ?");
			
			updateStatement.setInt(1, pallet.getPalletId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of pallet finished setting. Pallet number: " 
			      + pallet.getPalletId());
			
			updateStatement.close();
			DataServer.connection.commit();
			
			pallet.setFinished(true);
			
			return pallet;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of pallet finished setting. Pallet number: " 
					+ pallet.getPalletId());
			e.printStackTrace();
		}
	   
	   	return null;
   }
   
   @Override
   public Car executeFinishCar(Car car) throws RemoteException, SQLException {
	   try {
			PreparedStatement updateStatement = DataServer.connection.prepareStatement(
					"UPDATE car SET isFinished = 1 WHERE id = ?");
			
			updateStatement.setInt(1, car.getCarId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of car finished setting. Car number: " 
			      + car.getCarId());
			
			updateStatement.close();
			DataServer.connection.commit();
			
			car.setFinished(true);
			
			return car;
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of pallet car setting. Car number: " 
					+ car.getCarId());
			e.printStackTrace();
		}
	   
	   	return null;
   }

   @Override
   public PartList executeGetStolenParts(Car car) throws RemoteException,
         SQLException
   {
      try 
      {
         PreparedStatement statement = 
               DataServer.connection.prepareStatement("SELECT * FROM Part "
                                             + "WHERE carId = ?");
         
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
            
            System.out.println(part.getPartId());
         }

         statement.close();
         rs.close();
         
         System.out.println("[SUCCESS] Part List Retrieved");
         
         return partList;
      }
      catch (Exception e) {
         DataServer.connection.rollback();
         System.out.println("[FAIL] Part List Retrieval Failed");
         e.printStackTrace();
      }
      return null;
   }
   
   private static void addNonExistentEntities() throws SQLException {
	   try {
			PreparedStatement insertPalletStatement = DataServer.connection.prepareStatement(
					"INSERT INTO Pallet (id, partType, maxWeight, isFinished) "
					+ "SELECT -1, 'no pallet', 0, 0 "
					+ "FROM dual "
					+ "WHERE NOT EXISTS(SELECT id, partType, maxWeight, isFinished FROM pallet WHERE id = -1)");
			
			insertPalletStatement.execute();
			insertPalletStatement.close();
			
			PreparedStatement insertProductStatement = DataServer.connection.prepareStatement(
					"INSERT INTO Product (id, type, name) "
					+ "SELECT -1, 'no product', 'no product' "
					+ "FROM dual "
					+ "WHERE NOT EXISTS(SELECT id, type, name FROM product WHERE id = -1)");
			
			insertProductStatement.execute();
			insertProductStatement.close();
			
			DataServer.connection.commit();
		}
		catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of adding non existent entities. Exception: " 
					+ e.getMessage());
			e.printStackTrace();
		}
   }
   
   public static void main(String[] args) throws RemoteException
   {
      DataServer d = new DataServer();
      
      d.begin();
   }
}