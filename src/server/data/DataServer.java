package server.data;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import remote.interfaces.IDataServer;
import model.*;
import model.cache.CarCache;
import model.cache.PalletCache;
import model.cache.PartCache;
import model.cache.ProductCache;

public class DataServer extends UnicastRemoteObject implements IDataServer {
	private static final long serialVersionUID = 1L;
	private static Connection connection;

	public DataServer() throws RemoteException {
		super();
	}

	public void begin() {
		try {
			LocateRegistry.createRegistry(1099);
			Naming.rebind("dataServer", this);

			DataServer.connection = PersistenceConfig.establishConnection(connection);

			System.out.println("Data server is running... ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PartCache executeGetPartCache() throws RemoteException, SQLException {
		try {
			PreparedStatement selectStatement = DataServer.connection.prepareStatement(
					"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\", "
				        + "p.palletId AS \"partPalletId\", p.productId AS \"partProductId\", "
				        + "c.model AS \"carModel\", c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
				        + "c.weight as \"carWeight\", c.chassisNumber AS \"carChassisNumber\", c.state AS \"carState\" "
				    + "FROM part p INNER JOIN Car c ON (p.carId = c.id)");

			ResultSet resultSet = selectStatement.executeQuery();
			PartCache parts = new PartCache();

			while (resultSet.next()) {
				Part part = new Part();
				part.setPartId(resultSet.getInt("partId"));
				part.setType(resultSet.getString("partType"));
				part.setWeight(resultSet.getDouble("partWeight"));
				
				Car car = new Car();
				car.setChassisNumber(resultSet.getString("carChassisNumber"));
				car.setModel(resultSet.getString("carModel"));
				car.setManufacturer(resultSet.getString("carManufacturer"));
				car.setYear(resultSet.getInt("carYear"));
				car.setWeight(resultSet.getDouble("carWeight"));
				car.setState(resultSet.getString("carState"));
				part.setCar(car);

				part.setPalletId(resultSet.getInt("partPalletId"));
				part.setProductId(resultSet.getInt("partProductId"));				

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
	public CarCache executeGetCarCache() throws RemoteException, SQLException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT manufacturer, model, year, weight, chassisNumber, state FROM car");

			ResultSet carResultSet = selectStatement.executeQuery();
			CarCache cars = new CarCache();

			while (carResultSet.next()) {
				Car car = new Car();
				car.setModel(carResultSet.getString("model"));
				car.setManufacturer(carResultSet.getString("manufacturer"));
				car.setYear(carResultSet.getInt("year"));
				car.setWeight(carResultSet.getDouble("weight"));
				car.setChassisNumber(carResultSet.getString("chassisNumber"));
				car.setState(carResultSet.getString("state"));	

				cars.addCar(car);
			}

			selectStatement.close();
			carResultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of " + cars.count() + " cars from the database. ");

			return cars;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of cars retrieval. ");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public PalletCache executeGetPalletCache() throws RemoteException, SQLException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT id, partType, weight, maxWeight, state FROM pallet");

			ResultSet resultSet = selectStatement.executeQuery();
			PalletCache pallets = new PalletCache();

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
						"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\", "
					        + "p.productId AS \"partProductId\", c.model AS \"carModel\", "
					        + "c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
					        + "c.weight as \"carWeight\", c.chassisNumber AS \"carChassisNumber\", c.state AS \"carState\" "
					    + "FROM part p INNER JOIN Car c ON (p.carId = c.id) WHERE p.palletId = ?");
				
				partsSelectStatement.setInt(1, palletId);
				ResultSet partsResultSet = partsSelectStatement.executeQuery();

				while (partsResultSet.next()) {
					Part part = new Part();
					part.setPartId(partsResultSet.getInt("partId"));
					part.setType(partsResultSet.getString("partType"));
					part.setWeight(partsResultSet.getDouble("partWeight"));
					
					Car car = new Car();
					car.setChassisNumber(partsResultSet.getString("carChassisNumber"));
					car.setModel(partsResultSet.getString("carModel"));
					car.setManufacturer(partsResultSet.getString("carManufacturer"));
					car.setYear(partsResultSet.getInt("carYear"));
					car.setWeight(partsResultSet.getDouble("carWeight"));
					car.setState(partsResultSet.getString("carState"));
					part.setCar(car);

					part.setPalletId(palletId);
					part.setProductId(partsResultSet.getInt("partProductId"));				

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
	public ProductCache executeGetProductCache() throws RemoteException, SQLException {
		try {
			PreparedStatement selectStatement = DataServer.connection
					.prepareStatement("SELECT id, type, name FROM product");

			ResultSet resultSet = selectStatement.executeQuery();
			ProductCache products = new ProductCache();

			while (resultSet.next()) {
				Product product = new Product();
				int productId = resultSet.getInt("id");
				product.setProductId(productId);
				product.setType(resultSet.getString("type"));
				product.setName(resultSet.getString("name"));
				
				PartList productPartList = new PartList();
				
				PreparedStatement partsSelectStatement = DataServer.connection.prepareStatement(
						"SELECT p.id AS \"partId\", p.type AS \"partType\", p.weight AS \"partWeight\", "
					        + "p.palletId AS \"partPalletId\", c.model AS \"carModel\", "
					        + "c.manufacturer AS \"carManufacturer\", c.year AS \"carYear\", "
					        + "c.weight as \"carWeight\", c.chassisNumber AS \"carChassisNumber\", c.state AS \"carState\" "
					    + "FROM part p INNER JOIN Car c ON (p.carId = c.id) WHERE p.productId = ?");
				
				partsSelectStatement.setInt(1, productId);
				ResultSet partsResultSet = partsSelectStatement.executeQuery();

				while (partsResultSet.next()) {
					Part part = new Part();
					part.setPartId(partsResultSet.getInt("partId"));
					part.setType(partsResultSet.getString("partType"));
					part.setWeight(partsResultSet.getDouble("partWeight"));
					
					Car car = new Car();
					car.setChassisNumber(partsResultSet.getString("carChassisNumber"));
					car.setModel(partsResultSet.getString("carModel"));
					car.setManufacturer(partsResultSet.getString("carManufacturer"));
					car.setYear(partsResultSet.getInt("carYear"));
					car.setWeight(partsResultSet.getDouble("carWeight"));
					car.setState(partsResultSet.getString("carState"));
					part.setCar(car);

					part.setPalletId(partsResultSet.getInt("partPalletId"));
					part.setProductId(productId);				

					productPartList.addPart(part);
				}

				partsSelectStatement.close();
				partsResultSet.close();
				
				product.setPartList(productPartList);
				
				products.addProduct(product);
			}

			selectStatement.close();
			resultSet.close();
			System.out.println("[SUCCESS] Successful retrieval of " + products.count() + " products from the database. ");

			return products;
		} catch (Exception e) {
			System.out.println("[FAIL] Failed execution of products retrieval. ");
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
			
			if (pallet.getPartList().count() > 0) {
				pallet.getPartList().getList().forEach(part -> {
					try {
						PreparedStatement updateStatement = DataServer.connection
								.prepareStatement("UPDATE part SET palletId = ? WHERE id = ?");
						updateStatement.setInt(1, pallet.getPalletId());
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
					.prepareStatement("SELECT * FROM (SELECT id FROM pallet ORDER BY id DESC) WHERE ROWNUM = 1");

			ResultSet resultSet = returnStatement.executeQuery();

			while (resultSet.next()) {
				pallet.setPalletId(resultSet.getInt("id"));
			}

			System.out.println("[SUCCESS] Successful execution of new pallet registration. Pallet number: "
							+ pallet.getPalletId());

			returnStatement.close();

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

			return product;
		} catch (Exception e) {
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
			
			PreparedStatement carStatement = DataServer.connection
					.prepareStatement("SELECT * FROM (SELECT id FROM car WHERE chassisNumber = ?) "
									+ "WHERE ROWNUM = 1");
			
			carStatement.setString(1, part.getCar().getChassisNumber());
			ResultSet carResultSet = carStatement.executeQuery();
			
			int carId = -1;
			while (carResultSet.next()) {
				carId = carResultSet.getInt("id");
			}
			
			carResultSet.close();
			carStatement.close();

			PreparedStatement insertStatement = DataServer.connection
					.prepareStatement("INSERT INTO part (type, weight, carId, palletId, productId) VALUES "
							+ "(?, ?, ?, ?, ?)");

			insertStatement.setString(1, part.getType());
			insertStatement.setDouble(2, part.getWeight());
			insertStatement.setInt(3, carId);
			insertStatement.setInt(4, part.getPalletId());
			insertStatement.setInt(5, part.getProductId());
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
			DataServer.addNonExistentEntities();

			PreparedStatement updateStatement = DataServer.connection
					.prepareStatement("UPDATE part SET palletId = ? WHERE id = ?");

			updateStatement.setInt(1, pallet.getPalletId());
			updateStatement.setInt(2, part.getPartId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of part pallet setting. Part number: "
							+ part.getPartId());

			updateStatement.close();
			DataServer.connection.commit();

			part.setPalletId(pallet.getPalletId());

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
	public Part executeUpdatePartProduct(Part part, Product product) throws RemoteException, SQLException {
		try {
			DataServer.addNonExistentEntities();

			PreparedStatement updateStatement = DataServer.connection
					.prepareStatement("UPDATE part SET productId = ? WHERE id = ?");

			updateStatement.setInt(1, product.getProductId());
			updateStatement.setInt(2, part.getPartId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of part product setting. Part number: "
							+ part.getPartId());

			updateStatement.close();
			DataServer.connection.commit();

			part.setProductId(product.getProductId());

			return part;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out
					.println("[FAIL] Failed execution of part product setting. Part number: "
							+ part.getPartId());
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Pallet executeUpdatePalletWeight(Pallet pallet) throws RemoteException, SQLException {
		try {
			PreparedStatement updateStatement = DataServer.connection
					.prepareStatement("UPDATE pallet SET weight = ? WHERE id = ?");

			updateStatement.setDouble(1, pallet.getWeight());
			updateStatement.setInt(2, pallet.getPalletId());
			updateStatement.executeUpdate();
			updateStatement.close();

			System.out.println("[SUCCESS] Successful execution of pallet weight setting. Pallet number: "
							+ pallet.getPalletId());

			updateStatement.close();
			DataServer.connection.commit();

			return pallet;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of pallet weight setting. Pallet number: "
							+ pallet.getPalletId());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Pallet executeSetPalletState(Pallet pallet, String state) throws RemoteException, SQLException {
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

			pallet.setState(state);

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
	public Car executeSetCarState(Car car, String state) throws RemoteException, SQLException {
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

			car.setState(state);

			return car;
		} catch (Exception e) {
			DataServer.connection.rollback();
			System.out.println("[FAIL] Failed execution of car state setting. Car chassis: "
							+ car.getChassisNumber());
			e.printStackTrace();
		}

		return null;
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
}