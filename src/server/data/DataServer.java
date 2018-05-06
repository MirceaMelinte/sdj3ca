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
	private static Connection connection;
	private List<IObserver> observers = new ArrayList<>();

	public DataServer() throws RemoteException {
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
	
	@Override
	public Car executeGetCarByChassisNumber(String chassisNumber) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT c.model AS \"carModel\", c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
					        + "c.weight as \"carWeight\", c.state AS \"carState\" "
					    + "FROM car c WHERE c.chassisNumber = ?");
			selectStatement.setString(1, chassisNumber);
			
			ResultSet resultSet = selectStatement.executeQuery();
			Car car = new Car();

			while (resultSet.next()) {
				car.setChassisNumber(chassisNumber);
				car.setModel(resultSet.getString("carModel"));
				car.setManufacturer(resultSet.getString("carManufacturer"));
				car.setYear(resultSet.getInt("carYear"));
				car.setWeight(resultSet.getDouble("carWeight"));
				car.setState(resultSet.getString("carState"));
				
				PreparedStatement carIdSelectStatement = DataServer.connection.prepareStatement(
						"SELECT id FROM car WHERE chassisNumber = ?");
				carIdSelectStatement.setString(1, chassisNumber);
				ResultSet carIdResultSet = carIdSelectStatement.executeQuery();
				int carId = -1;
				
				while (carIdResultSet.next()) {
					carId = carIdResultSet.getInt("id");
				}
				
				carIdResultSet.close();
				carIdSelectStatement.close();
				
				PartList carPartList = new PartList();
				
				PreparedStatement partsSelectStatement = DataServer.connection.prepareStatement(
						"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\" "
					    + "FROM part p WHERE p.carId = ?");
				
				partsSelectStatement.setInt(1, carId);
				ResultSet partsResultSet = partsSelectStatement.executeQuery();

				while (partsResultSet.next()) {
					Part newPart = new Part();
					newPart.setPartId(partsResultSet.getInt("partId"));
					newPart.setType(partsResultSet.getString("partType"));
					newPart.setWeight(partsResultSet.getDouble("partWeight"));
					
					carPartList.addPart(newPart);
				}

				partsSelectStatement.close();
				partsResultSet.close();
				
				car.setPartList(carPartList);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of car " + car.getChassisNumber());

			return car;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of car retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public PalletList executeGetAvailablePallets(Part part) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT pa.id, pa.partType, pa.weight, pa.maxWeight, pa.state "
							+ "FROM pallet pa INNER JOIN part pt ON (pa.id = pt.palletId) "
							+ "WHERE pa.state = 'Available'");

			ResultSet resultSet = selectStatement.executeQuery();
			PalletList pallets = new PalletList();
			
			while (resultSet.next()) {
				Pallet pallet = new Pallet();
				int palletId = resultSet.getInt("id");
				pallet.setPalletId(palletId);
				pallet.setPartType(resultSet.getString("partType"));
				pallet.setWeight(resultSet.getDouble("weight"));
				pallet.setMaxWeight(resultSet.getDouble("maxWeight"));
				pallet.setState(resultSet.getString("state"));
				
				PartList palletPartList = new PartList();
				
				PreparedStatement partsSelectStatement = DataServer.connection.prepareStatement(
						"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\" "
					    + "FROM part p WHERE p.palletId = ?");
				
				partsSelectStatement.setInt(1, palletId);
				ResultSet partsResultSet = partsSelectStatement.executeQuery();

				while (partsResultSet.next()) {
					Part newPart = new Part();
					newPart.setPartId(partsResultSet.getInt("partId"));
					newPart.setType(partsResultSet.getString("partType"));
					newPart.setWeight(partsResultSet.getDouble("partWeight"));
					
					palletPartList.addPart(part);
				}

				partsSelectStatement.close();
				partsResultSet.close();
				
				pallet.setPartList(palletPartList);
				pallets.addPallet(pallet);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of " + pallets.count() + " pallets from the database. ");

			return pallets;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of pallets retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public CarList executeGetAvailableCars() throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT c.model AS \"carModel\", c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
					        + "c.weight as \"carWeight\", c.chassisNumber AS \"carChassisNumber\", c.state AS \"carState\" "
					    + "FROM car c WHERE c.state = 'Finished'");
			
			ResultSet resultSet = selectStatement.executeQuery();
			CarList cars = new CarList();

			while (resultSet.next()) {
				Car car = new Car();
				car.setChassisNumber(resultSet.getString("carChassisNumber"));
				car.setModel(resultSet.getString("carModel"));
				car.setManufacturer(resultSet.getString("carManufacturer"));
				car.setYear(resultSet.getInt("carYear"));
				car.setWeight(resultSet.getDouble("carWeight"));
				car.setState(resultSet.getString("carState"));
				cars.addCar(car);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of available cars. ");

			return cars;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of available cars retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Pallet executeGetPalletById(int palletId) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT partType, weight, maxWeight, state FROM pallet");

			ResultSet resultSet = selectStatement.executeQuery();
			Pallet pallet = new Pallet();
			
			while (resultSet.next()) {
				pallet.setPalletId(palletId);
				pallet.setPartType(resultSet.getString("partType"));
				pallet.setWeight(resultSet.getDouble("weight"));
				pallet.setMaxWeight(resultSet.getDouble("maxWeight"));
				pallet.setState(resultSet.getString("state"));
				
				PartList palletPartList = new PartList();
				
				PreparedStatement partsSelectStatement = DataServer.connection.prepareStatement(
						"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\" "
					    + "FROM part p WHERE p.palletId = ?");
				
				partsSelectStatement.setInt(1, palletId);
				ResultSet partsResultSet = partsSelectStatement.executeQuery();

				while (partsResultSet.next()) {
					Part part = new Part();
					part.setPartId(partsResultSet.getInt("partId"));
					part.setType(partsResultSet.getString("partType"));
					part.setWeight(partsResultSet.getDouble("partWeight"));
					
					palletPartList.addPart(part);
				}

				partsSelectStatement.close();
				partsResultSet.close();
				
				pallet.setPartList(palletPartList);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of pallet #" + pallet.getPalletId() + " from the database. ");

			return pallet;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of pallets retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Part executeGetPartById(int partId) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT p.type AS \"partType\", p.weight AS \"partWeight\" "
				    + "FROM part p WHERE p.id = ?");
			selectStatement.setInt(1, partId);
			
			ResultSet resultSet = selectStatement.executeQuery();
			Part part = new Part();
			
			while (resultSet.next()) {
				part.setPartId(partId);
				part.setType(resultSet.getString("partType"));
				part.setWeight(resultSet.getDouble("partWeight"));
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of part #" + part.getPartId() + " from the database. ");

			return part;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of part retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Car executeGetCarByPart(int partId) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT c.model AS \"carModel\", c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
					        + "c.weight as \"carWeight\", c.chassisNumber AS \"carChassisNumber\", c.state AS \"carState\" "
					    + "FROM car c INNER JOIN part p ON (p.carId = c.id) WHERE p.id = ?");
			selectStatement.setInt(1, partId);
			
			ResultSet resultSet = selectStatement.executeQuery();
			Car car = new Car();

			while (resultSet.next()) {
				car.setChassisNumber(resultSet.getString("carChassisNumber"));
				car.setModel(resultSet.getString("carModel"));
				car.setManufacturer(resultSet.getString("carManufacturer"));
				car.setYear(resultSet.getInt("carYear"));
				car.setWeight(resultSet.getDouble("carWeight"));
				car.setState(resultSet.getString("carState"));
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of car " + car.getChassisNumber());

			return car;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of car retrieval. ");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public PartList executeGetPartsByProduct(int productId) throws RemoteException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\""
				    + "FROM part p WHERE p.productId = ?");
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
	public Pallet executeRegisterPallet(Pallet pallet) throws RemoteException, SQLException {
		try {
			PreparedStatement insertStatement = DataServer.connection
					.prepareStatement("INSERT INTO pallet (partType, weight, maxWeight, state) "
							+ "VALUES (?, ?, ?, ?)");

			insertStatement.setString(1, pallet.getPartType());
			insertStatement.setDouble(2, pallet.getWeight());
			insertStatement.setDouble(3, pallet.getMaxWeight());
			insertStatement.setString(4, pallet.getState());
			insertStatement.execute();
			insertStatement.close();

			DataServer.connection.commit();
			
			PreparedStatement returnStatement = DataServer.connection
					.prepareStatement("SELECT * FROM (SELECT id FROM pallet ORDER BY id DESC) WHERE ROWNUM = 1");

			ResultSet resultSet = returnStatement.executeQuery();

			while (resultSet.next()) {
				pallet.setPalletId(resultSet.getInt("id"));
			}

			System.out.println("[SUCCESS] Successful execution of new pallet registration. Pallet number: "
							+ pallet.getPalletId());

			returnStatement.close();

			notifyObservers(new Transaction<Pallet>(Transaction.REGISTER_PALLET, pallet));
			
			return pallet;
		} catch (Exception e) {
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
			PreparedStatement insertStatement = DataServer.connection
					.prepareStatement("INSERT INTO product (type, name) VALUES (?, ?)");

			insertStatement.setString(1, product.getType());
			insertStatement.setString(2, product.getName());
			insertStatement.execute();
			insertStatement.close();
			
			if (product.getPartList().count() > 0) {
				product.getPartList().getList().forEach(part -> {
					try {
						PreparedStatement updateStatement = DataServer.connection
								.prepareStatement("UPDATE part SET productId = ? WHERE id = ?");
						updateStatement.setInt(1, product.getProductId());
						updateStatement.setInt(2, part.getPartId());
						updateStatement.executeUpdate();
						updateStatement.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			DataServer.connection.commit();
			
			PreparedStatement returnStatement = DataServer.connection
					.prepareStatement("SELECT * FROM (SELECT id FROM product ORDER BY id DESC) WHERE ROWNUM = 1");

			ResultSet resultSet = returnStatement.executeQuery();

			while (resultSet.next()) {
				product.setProductId(resultSet.getInt("id"));
			}

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
	public	Pallet executeUpdateFinishPallet(Pallet pallet) throws RemoteException, SQLException {
		pallet = executeUpdatePalletState(pallet, Pallet.FINISHED);
		
		notifyObservers(new Transaction<Pallet>(Transaction.UPDATE_FINISH_PALLET, pallet));

		return pallet;
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
	public Car executeUpdateFinishCar(Car car) throws RemoteException, SQLException {
		car =  executeUpdateCarState(car, Car.FINISHED);
		
		notifyObservers(new Transaction<Car>(Transaction.UPDATE_FINISH_CAR, car));
		
		return car;
		
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
   public PartList executeGetPartsByCar(String chassisNumber) throws RemoteException,
         SQLException
   {
      try 
      {
         PreparedStatement statement = 
               DataServer.connection.prepareStatement("SELECT * FROM Part "
                                             + "WHERE carId = (SELECT carId FROM Car WHERE chassisNumber = ?)");
         
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
         
         System.out.println("[SUCCESS] Part List Retrieved");
         
         return partList;
      }
      catch (SQLException e) {
         System.out.println("[FAIL] Part List Retrieval Failed");
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
            DataServer.connection.prepareStatement("SELECT * FROM Part "
                  + "WHERE carId = (SELECT carId FROM Car WHERE chassisNumber = ?)");
      
      PreparedStatement productStatement = 
            DataServer.connection.prepareStatement("SELECT * FROM Product "
                  + "WHERE id in (SELECT productId FROM Part "
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
      
      System.out.println("[SUCCESS] Product List Retrieved");
      
      return productList;
      
   }
   catch (SQLException e) {
      System.out.println("[FAIL] Product List Retrieval Failed");
      e.printStackTrace();
   }
   return null;
   }

   @Override
   public void attatch(IObserver observer) throws RemoteException
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
                     + " (SELECT id, type, name FROM product WHERE id = -1)");

         insertProductStatement.execute();
         insertProductStatement.close();

         DataServer.connection.commit();
      } catch (Exception e) {
         DataServer.connection.rollback();
         System.out
               .println("[FAIL] Failed execution of adding non existent entities. Exception: "
                     + e.getMessage());
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) throws RemoteException {
      DataServer d = new DataServer();

      d.begin();
   }

   @Override
   public CarList executeGetAllCars() throws RemoteException, SQLException
   {
      try
      {
      PreparedStatement carStatement = 
            DataServer.connection.prepareStatement("SELECT c.chassisNumber AS \"chassisNumber\", c.model AS \"carModel\", c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
                  + "c.weight as \"carWeight\", c.state AS \"carState\" "
              + "FROM Car c");     
            
      ResultSet resultSet = carStatement.executeQuery();
      CarList carList = new CarList();
      
      while(resultSet.next())
      {
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
         System.out.println("[SUCCESS] Car List Retrieved");
         return carList;
      }
      catch (Exception e) {
         System.out
               .println("[FAIL] Failed get car list. Exception: "
                     + e.getMessage());
         e.printStackTrace();
      }
      return null;
   }

   @Override
   public PartList executeGetAllParts() throws RemoteException, SQLException
   {
      try {
         PreparedStatement selectStatement = DataServer.connection.prepareStatement(
               "SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\" "
                + "FROM part p");        
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
         System.out.println("[SUCCESS] Successful retrieval of part list from the database. ");

         return partList;
      } catch (Exception e) {
         System.out.println("[FAIL] Failed execution of part list retrieval. ");
         e.printStackTrace();
      }

      return null;
   }

   @Override
   public PalletList executeGetAllPallets() throws RemoteException, SQLException
   {
      try
      {   
      PreparedStatement palletStatement = 
            DataServer.connection.prepareStatement("SELECT * FROM Pallet");
       
      ResultSet palletResultSet = palletStatement.executeQuery();
      
      PalletList palletList = new PalletList();
      
      while(palletResultSet.next())
      {
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
      
      System.out.println("[SUCCESS] Pallet List Retrieved");
      
      return palletList;
      
   }
   catch (SQLException e) {
      System.out.println("[FAIL] Pallet List Retrieval Failed");
      e.printStackTrace();
   }
   return null;
   }

   @Override
   public ProductList executeGetAllProducts() throws RemoteException, SQLException
   {
      try
      {    
      PreparedStatement productStatement = 
            DataServer.connection.prepareStatement("SELECT * FROM Product");
       
      ResultSet productResultSet = productStatement.executeQuery();
      
      ProductList productList = new ProductList();
      
      while(productResultSet.next())
      {
         Product product = new Product();
         product.setProductId(productResultSet.getInt("id"));
         product.setName(productResultSet.getString("name"));
         product.setType(productResultSet.getString("type"));
         PartList partList = executeGetPartsByProduct(product.getProductId());
         product.setPartList(partList);
         productList.addProduct(product);
      }
      
       
      System.out.println(productList.toString());

      productStatement.close();
      productResultSet.close();
      
      System.out.println("[SUCCESS] Product List Retrieved");
      
      return productList;
      
   }
   catch (SQLException e) {
      System.out.println("[FAIL] Product List Retrieval Failed");
      e.printStackTrace();
   }
   return null;
   }

   @Override
   public PartList executeGetPartsByPallet(int palletId)
         throws RemoteException, SQLException
   {
      try {
         PreparedStatement selectStatement = DataServer.connection.prepareStatement(
               "SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\""
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
}