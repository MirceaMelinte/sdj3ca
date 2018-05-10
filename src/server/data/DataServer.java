package server.data;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Car;
import model.CarList;
import model.Pallet;
import model.PalletList;
import model.Part;
import model.PartList;
import model.Product;
import model.ProductList;
import model.Transaction;
import remote.interfaces.IDataServer;
import remote.interfaces.IObserver;

public class DataServer extends UnicastRemoteObject implements IDataServer {
	private static final long serialVersionUID = 1L;
	private static volatile DataServer instance = null;
	private static Connection connection;
	private List<IObserver> observers = new ArrayList<>();

	private DataServer() throws RemoteException {
		super();	
	}

	public void begin() {
		try {
			LocateRegistry.createRegistry(1099);
			Naming.rebind("dataServer", this);
			DataServer.connection = PersistenceConfig.establishConnection(connection);
			System.out.println("Data server is running... ");
			DataServer.addNonExistentEntities();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataServer getInstance() throws RemoteException {
		if (DataServer.instance == null) {
			synchronized (DataServer.class) {
				if (DataServer.instance == null) {
					DataServer.instance = new DataServer();
				}
			}
		}
		
		return DataServer.instance;
	}
	
	// GET
	
	@Override
	public CarList executeGetAllCars() throws RemoteException, SQLException {
		try {
			PreparedStatement carStatement = DataServer.connection
					.prepareStatement("SELECT c.chassisNumber AS \"chassisNumber\", c.model AS \"carModel\", c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
							+ "c.weight as \"carWeight\", c.state AS \"carState\" FROM Car c");

			ResultSet resultSet = carStatement.executeQuery();
			CarList carList = new CarList();

			while (resultSet.next()) {
				Car car = new Car();
				car.setChassisNumber(resultSet.getString("chassisNumber"));
				car.setModel(resultSet.getString("carModel"));
				car.setManufacturer(resultSet.getString("carManufacturer"));
				car.setYear(resultSet.getInt("carYear"));
				car.setWeight(resultSet.getDouble("carWeight"));
				car.setState(resultSet.getString("carState"));
				PartList partList = executeGetPartsByCar(car.getChassisNumber());
				car.setPartList(partList);
				carList.addCar(car);
			}
			
			resultSet.close();
			carStatement.close();
			
			System.out.println("[SUCCESS] Successful retrieval of " + carList.count() + " cars from the database. ");
			
			return carList;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of part list retrieval. ");
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public PartList executeGetAllParts() throws RemoteException, SQLException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\" FROM part p");
			ResultSet resultSet = selectStatement.executeQuery();
			PartList partList = new PartList();

			while (resultSet.next()) {
				Part part = new Part();
				part.setPartId(resultSet.getInt("partId"));
				part.setType(resultSet.getString("partType"));
				part.setWeight(resultSet.getDouble("partWeight"));
				partList.addPart(part);
			}

			selectStatement.close();
			resultSet.close();
			
			System.out.println("[SUCCESS] Successful retrieval of " + partList.count() + " parts from the database. ");

			return partList;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of part list retrieval. ");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public PalletList executeGetAllPallets() throws RemoteException, SQLException {
		try {
			PreparedStatement palletStatement = DataServer.connection
					.prepareStatement("SELECT id, partType, weight, maxWeight, state FROM pallet");

			ResultSet palletResultSet = palletStatement.executeQuery();

			PalletList palletList = new PalletList();

			while (palletResultSet.next()) {
				Pallet pallet = new Pallet();
				pallet.setPalletId(palletResultSet.getInt("id"));
				pallet.setPartType(palletResultSet.getString("partType"));
				pallet.setWeight(palletResultSet.getDouble("weight"));
				pallet.setMaxWeight(palletResultSet.getDouble("maxWeight"));
				pallet.setState(palletResultSet.getString("state"));
				
				PartList partList = executeGetPartsByPallet(pallet.getPalletId());
				pallet.setPartList(partList);
				palletList.addPallet(pallet);
			}

			palletStatement.close();
			palletResultSet.close();

			System.out.println("[SUCCESS] Successful retrieval of " + palletList.count() + " pallets from the database. ");

			return palletList;

		} catch (SQLException e) {
			System.out.println("[FAIL] Failed execution of pallets retrieval. ");
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public ProductList executeGetAllProducts() throws RemoteException, SQLException {
		try {
			PreparedStatement productStatement = DataServer.connection.prepareStatement("SELECT id, name, type FROM Product");

			ResultSet productResultSet = productStatement.executeQuery();

			ProductList productList = new ProductList();

			while (productResultSet.next()) {
				Product product = new Product();
				product.setProductId(productResultSet.getInt("id"));
				product.setName(productResultSet.getString("name"));
				product.setType(productResultSet.getString("type"));
				PartList partList = executeGetPartsByProduct(product.getProductId());
				product.setPartList(partList);
				productList.addProduct(product);
			}

			productStatement.close();
			productResultSet.close();

			System.out.println("[SUCCESS] Successful retrieval of " + productList.count() + " products from the database. ");

			return productList;

		} catch (SQLException e) {
			System.out.println("[FAIL] Product List Retrieval Failed");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PartList executeGetPartsByPallet(int palletId) throws RemoteException, SQLException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\""
							+ "FROM part p WHERE p.palletId = ?");
			selectStatement.setInt(1, palletId);

			ResultSet resultSet = selectStatement.executeQuery();
			PartList parts = new PartList();

			while (resultSet.next()) {
				Part part = new Part();
				part.setPartId(resultSet.getInt("partId"));
				part.setType(resultSet.getString("partType"));
				part.setWeight(resultSet.getDouble("partWeight"));

				parts.addPart(part);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of " + parts.count() + " parts from the database. ");

			return parts;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of parts retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public PartList executeGetPartsByProduct(int productId) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT id AS \"partId\", type AS \"partType\", weight AS \"partWeight\""
				    + "FROM part WHERE productId = ?");
			selectStatement.setInt(1, productId);
			
			ResultSet resultSet = selectStatement.executeQuery();
			PartList parts = new PartList();

			while (resultSet.next()) {
				Part part = new Part();
				part.setPartId(resultSet.getInt("partId"));
				part.setType(resultSet.getString("partType"));
				part.setWeight(resultSet.getDouble("partWeight"));
				
				parts.addPart(part);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of " + parts.count() + " parts from the database. ");

			return parts;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of parts retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	   @Override
	   public PartList executeGetPartsByCar(String chassisNumber) throws RemoteException, SQLException
	   {
	      try 
	      {
	         PreparedStatement statement = 
	               DataServer.connection.prepareStatement("SELECT id, type, weight FROM Part "
	                                             + "WHERE carId = (SELECT id FROM Car WHERE chassisNumber = ?)");
	         
	         statement.setString(1, chassisNumber);
	         ResultSet resultSet = statement.executeQuery();
	         PartList partList = new PartList();
	         
	         while(resultSet.next())
	         {
	            Part part = new Part();
	            part.setPartId(resultSet.getInt("id"));
	            part.setType(resultSet.getString("type"));
	            part.setWeight(resultSet.getDouble("weight"));
	            partList.addPart(part);
	         }

	         statement.close();
	         resultSet.close();

	         System.out.println("[SUCCESS] Successful retrieval of " + partList.count() + " parts from the database. ");
	         
	         return partList;
	      }
	      catch (SQLException e) {
	    	 System.out.println("[FAIL] Failed execution of parts retrieval. ");
	         e.printStackTrace();
	      }
	      
	      return null;
	   }

	   @Override
	   public ProductList executeGetStolenProducts(String chassisNumber) throws RemoteException,
	         SQLException
	   {
	      try
	      {
	      PreparedStatement partStatement = 
	            DataServer.connection.prepareStatement("SELECT id, type, weight, productId FROM Part "
	                  + "WHERE carId = (SELECT carId FROM Car WHERE chassisNumber = ?)");
	      
	      PreparedStatement productStatement = 
	            DataServer.connection.prepareStatement("SELECT id, name, type FROM Product "
	                  + "WHERE id IN (SELECT productId FROM Part "
	                  + "WHERE carId = (SELECT carId FROM Car WHERE chassisNumber = ?))");
	       
	      partStatement.setString(1, chassisNumber);
	      productStatement.setString(1, chassisNumber);
	      ResultSet partResultSet = partStatement.executeQuery();
	      ResultSet productResultSet = productStatement.executeQuery();
	      
	      ProductList productList = new ProductList();
	      
	      while(productResultSet.next())
	      {
	         Product product = new Product();
	         product.setProductId(productResultSet.getInt("id"));
	         product.setName(productResultSet.getString("name"));
	         product.setType(productResultSet.getString("type"));
	         productList.addProduct(product);
	      }
	      
	      while(partResultSet.next())
	      {
	         Part part = new Part();
	         part.setPartId(partResultSet.getInt("id"));
	         part.setType(partResultSet.getString("type"));
	         part.setWeight(partResultSet.getDouble("weight"));
	         productList.getList().forEach((x) -> {
	            try
	            {
	               if(x.getProductId() == partResultSet.getInt("productId"))
	               {
	                  x.getPartList().addPart(part);
	               }
	            }
	            catch (Exception e)
	            {
	               e.printStackTrace();
	            }
	         });
	      }
	       
	      System.out.println(productList.toString());

	      partStatement.close();
	      partResultSet.close();
	      productStatement.close();
	      productResultSet.close();

	      System.out.println("[SUCCESS] Successful retrieval of " + productList.count() + " products from the database. ");
	      
	      return productList;
	      
	   }
	   catch (SQLException e) {
		  System.out.println("[FAIL] Failed execution of products retrieval. ");
	      e.printStackTrace();
	   }
	   
	   return null;
	}
	
	// INSERT

	@Override
	public Car executeRegisterCar(Car car) throws SQLException, RemoteException {
		try {
			PreparedStatement insertStatement = DataServer.connection
					.prepareStatement("INSERT INTO car (manufacturer, model, year, weight, chassisNumber, state) "
							+ "VALUES (?, ?, ?, ?, ?, ?)");

			insertStatement.setString(1, car.getManufacturer());
			insertStatement.setString(2, car.getModel());
			insertStatement.setInt(3, car.getYear());
			insertStatement.setDouble(4, car.getWeight());
			insertStatement.setString(5, car.getChassisNumber());
			insertStatement.setString(6, car.getState());
			insertStatement.execute();
			insertStatement.close();

			System.out.println("[SUCCESS] Successful execution of new car registration. Chassis number: "
							+ car.getChassisNumber());
			DataServer.connection.commit();		

			notifyObservers(new Transaction<Car>(Transaction.REGISTER_CAR, car));
			
			return car;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out
					.println("[FAIL] Failed execution of new car registration. Chassis number: "
							+ car.getChassisNumber());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Product executeRegisterProduct(Product product) throws RemoteException, SQLException {
		try {
			PreparedStatement insertStatement = DataServer.connection
					.prepareStatement("INSERT INTO product (type, name) VALUES (?, ?)");

			insertStatement.setString(1, product.getType());
			insertStatement.setString(2, product.getName());
			insertStatement.execute();
			insertStatement.close();
			
			PreparedStatement returnStatement = DataServer.connection
               .prepareStatement("SELECT * FROM (SELECT id FROM product ORDER BY id DESC) WHERE ROWNUM = 1");

         ResultSet resultSet = returnStatement.executeQuery();

         while (resultSet.next()) {
            product.setProductId(resultSet.getInt("id"));
         }
			
			System.out.println(product.toString());
			
			if (product.getPartList().count() > 0) {
				product.getPartList().getList().forEach(part -> {
					try {
						PreparedStatement updateStatement = DataServer.connection
								.prepareStatement("UPDATE part SET productId = ? WHERE id = ?");
						updateStatement.setInt(1, product.getProductId());
						updateStatement.setInt(2, part.getPartId());
						updateStatement.executeUpdate();
						updateStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			}

			DataServer.connection.commit();

			System.out.println("[SUCCESS] Successful execution of new product registration. Product number: "
							+ product.getProductId());

			returnStatement.close();

			
			/*
			 * Product needs to have 
			 * - productid, type, name
			 * - part list with part that contain only ids of parts
			 */
			notifyObservers(new Transaction<Product>(Transaction.REGISTER_PRODUCT, product));
			
			return product;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of new product registration. Product number: "
							+ product.getProductId());
			e.printStackTrace();
		}

		return null;
	}

	
	
	// Part that is returned holds information about newly created part ID that is then sent to the client
	@Override
	public Part executeRegisterNewPart(Part part, String chassisNumber) throws RemoteException, SQLException {
		try {
		   
			PreparedStatement insertStatement = DataServer.connection
					.prepareStatement("INSERT INTO part (type, weight, carId, palletId, productId) VALUES "
					      + "(?, ?, (SELECT id FROM Car WHERE chassisNumber = ?), -1, -1)");

			insertStatement.setString(1, part.getType());
			insertStatement.setDouble(2, part.getWeight());
			insertStatement.setString(3, chassisNumber);
			insertStatement.execute();
			insertStatement.close();

			DataServer.connection.commit();
			
			
			PreparedStatement returnStatement = DataServer.connection
					.prepareStatement("SELECT * FROM (SELECT id FROM part ORDER BY id DESC) WHERE ROWNUM = 1");

			ResultSet resultSet = returnStatement.executeQuery();

			while (resultSet.next()) {
				part.setPartId(resultSet.getInt("id"));
			}

			System.out.println("[SUCCESS] Successful execution of new part registration. Part number: "
							+ part.getPartId());
			
			resultSet.close();
			returnStatement.close();
			
			Car car = new Car();
			car.setChassisNumber(chassisNumber);
			car.getPartList().addPart(part);
			car.setState(Car.IN_PROGRESS);	
			
			executeUpdateCarState(car, Car.IN_PROGRESS);

			/*
			 * Car needs to have 
			 * - chassisNumber defined
			 * - only one part in Part List
			 * - state defined
			 */
			
			notifyObservers(new Transaction<Car>(Transaction.REGISTER_PART, car));
			
			return part;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of new part registration. Part number: "
							+ part.getPartId());
			e.printStackTrace();
		}

		return null;
	}
	
	// UPDATE

	@Override
	public Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException {
		try {
			PreparedStatement updateStatement = DataServer.connection
					.prepareStatement("UPDATE part SET palletId = ? WHERE id = ?");

			updateStatement.setInt(1, pallet.getPalletId());
			updateStatement.setInt(2, part.getPartId());
			updateStatement.executeUpdate();
			updateStatement.close();
			
			PreparedStatement updateWeightStatement = DataServer.connection
               .prepareStatement("UPDATE pallet SET weight = ? WHERE id = ?");

			updateWeightStatement.setDouble(1, pallet.getWeight());
			updateWeightStatement.setInt(2, pallet.getPalletId());
			updateWeightStatement.executeUpdate();
			updateWeightStatement.close();

         System.out.println("[SUCCESS] Successful execution of pallet weight setting. Pallet number: "
                     + pallet.getPalletId());

         updateStatement.close();
         DataServer.connection.commit();

			System.out.println("[SUCCESS] Successful execution of part pallet setting. Part number: "
							+ part.getPartId());

			updateStatement.close();
			DataServer.connection.commit();

			/*
			 * this should have a Pallet in transaction
			 * pallet needs to have 
			 * - pallteID defined
			 * - one new part in Part List
			 * - updated part weight
			 * - part type defined ( so in case it was undefined and first part was put it can be changed also)
			 */
			
			// i am creating a new pallet because pallet from parameter could have partList with many parts already
			
			Pallet palletNotify = new Pallet();
			palletNotify.setPalletId(pallet.getPalletId());
			palletNotify.setWeight(pallet.getWeight() + part.getWeight());
			palletNotify.setPartType(part.getType());
			palletNotify.getPartList().addPart(part);
			notifyObservers(new Transaction<Pallet>(Transaction.UPDATE_PALLET_PART, palletNotify));
			
			return part;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of part pallet setting. Part number: "
							+ part.getPartId());
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Pallet executeUpdatePalletState(Pallet pallet, String state) throws RemoteException, SQLException {
		try {
			PreparedStatement updateStatement = DataServer.connection
					.prepareStatement("UPDATE pallet SET state = ? WHERE id = ?");

			updateStatement.setString(1, state);
			updateStatement.setInt(2, pallet.getPalletId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of pallet state setting. Pallet number: "
							+ pallet.getPalletId());

			updateStatement.close();
			DataServer.connection.commit();

			if (state.equals(Pallet.FINISHED))
				notifyObservers(new Transaction<Pallet>(Transaction.UPDATE_FINISH_PALLET, pallet));

			return pallet;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of pallet state setting. Pallet number: "
							+ pallet.getPalletId());
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Car executeUpdateCarState(Car car, String state) throws RemoteException, SQLException {
		try {
			PreparedStatement updateStatement = DataServer.connection
					.prepareStatement("UPDATE car SET state = ? WHERE chassisNumber = ?");

			updateStatement.setString(1, state);
			updateStatement.setString(2, car.getChassisNumber());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of car state setting. Car chassis: "
							+ car.getChassisNumber());

			updateStatement.close();
			DataServer.connection.commit();
			
			if (state.equals(Car.FINISHED))
				notifyObservers(new Transaction<Car>(Transaction.UPDATE_FINISH_CAR, car));
			
			return car;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of car state setting. Car chassis: "
							+ car.getChassisNumber());
			e.printStackTrace();
		}

		return null;
	}

   @Override
   public void attach(IObserver observer) throws RemoteException
   {
      observers.add(observer);
   }

   @Override
   public void dettach(IObserver observer) throws RemoteException
   {
      observers.remove(observer);
   }
   
   private <T> void notifyObservers(Transaction<T> t)
   {
      observers.forEach((observer) -> {
         try {
            observer.update(t);
            System.out.println("[SUCESS] Observers have been notified about new " + 
                  t.getLoad().getClass().getSimpleName() + " registration");
         }
         catch (Exception e) {
            System.out.println("[FAIL] Observers have been not notified about new " + 
                  t.getLoad().getClass().getSimpleName() + " registration");
            e.printStackTrace();
         }
      });
   }
   
   private static void addNonExistentEntities() throws SQLException {
      try {
         PreparedStatement insertPalletStatement = DataServer.connection
               .prepareStatement("INSERT INTO Pallet (id, partType, weight, maxWeight, state) "
                     + "SELECT -1, 'no pallet', 0, 0, 'Finished' "
                     + "FROM dual "
                     + "WHERE NOT EXISTS "
                     + "(SELECT id, partType, weight, maxWeight, state FROM pallet WHERE id = -1)");

         insertPalletStatement.execute();
         insertPalletStatement.close();

         PreparedStatement insertProductStatement = DataServer.connection
               .prepareStatement("INSERT INTO Product (id, type, name) "
                     + "SELECT -1, 'no product', 'no product' "
                     + "FROM dual "
                     + "WHERE NOT EXISTS "
                     + "(SELECT id, type, name FROM product WHERE id = -1)");

         insertProductStatement.execute();
         insertProductStatement.close();

         DataServer.connection.commit();
      } catch (Exception e) {
         DataServer.connection.rollback();
         System.out.println("[FAIL] Failed execution of adding non existent entities. ");
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) throws RemoteException {
	   DataServer.getInstance().begin();
   }
}