package server.logic;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import common.Validation;
import remote.interfaces.*;
import model.*;
import model.cache.*;

public class LogicServer extends UnicastRemoteObject implements ILogicServer {
	private static final long serialVersionUID = 1L;
	private IDataServer dataServer;

	private CarList availableCars;
	private PalletList availablePallets;
	private Cache cacheMemory;

	public LogicServer() throws RemoteException {
		super();
	}

	public void begin() throws RemoteException {
		try {
			LocateRegistry.getRegistry(1099);

			Naming.rebind("logicServer", this);

			String ip = "localhost";
			String URL = "rmi://" + ip + "/" + "dataServer";

			this.dataServer = (IDataServer) Naming.lookup(URL);

			System.out.println("Logic server is running... ");

			this.cacheMemory = new Cache();
			this.synchronizeServerCaches();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RemoteException {
		LogicServer l = new LogicServer();
		l.begin();
	}

	// FIRST CLIENT

	@Override
	public String validateRegisterCar(Car car) throws RemoteException {

		// Validation

		if (!Validation.validate(car.getChassisNumber(), Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car chassis number. ";

		if (!Validation.validate(car.getYear(), Validation.YEAR))
			return "[VALIDATION ERROR] Invalid car registration year. ";

		if (!car.getState().equals(Car.AVAILABLE))
			return "[VALIDATION ERROR] Invalid car state. ";

		// Update Database

		try {
			car = dataServer.executeRegisterCar(car);

			if (car == null)
				return "[APPLICATION FAILURE] The car registration has failed. ";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getCarCache().addCar(car);
		return "[SUCCESS] The car was registered.";

	}

	// SECOND CLIENT

	/*
	 * Also returns in progress cars...
	 */
	@Override
	public CarList getAvailableCars() throws RemoteException {

		CarList carList = new CarList();

		for (Car car : this.cacheMemory.getCarCache().getCache().values())
			if (!car.getState().equals(Car.FINISHED))
				carList.addCar(car);

		return carList;

	}

	/*
	 * Car object inside a part only has only chassis number set ... is that a
	 * problem?
	 */
	@Override
	public String validateRegisterPart(Part part) throws RemoteException {

		// Validate

		if (part.getType() == null || part.getType().isEmpty())
			return "[VALIDATION ERROR] Invalid part type.";

		if (part.getWeight() <= 0)
			return "[VALIDATION ERROR] Invalid part weight.";

		if (part.getCar() == null)
			return "[VALIDATION ERROR] There was no car set for this part.";

		if (!Validation.validate(part.getCar().getChassisNumber(), Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car chassis number.";

		if (!this.cacheMemory.getCarCache().contains(part.getCar().getChassisNumber()))
			return "[VALIDATION ERROR] This car does not exist.";

		if (this.cacheMemory.getCarCache().getCar(part.getCar().getChassisNumber()).getState().equals(Car.FINISHED))
			return "[VALIDATION ERROR] This car is already finished.";

		// Update Database

		try {
			part = dataServer.executeRegisterNewPart(part);

			if (part == null)
				return "[FAIL]";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		Car car = this.cacheMemory.getCarCache().getCar(part.getCar().getChassisNumber());
		car.setState(Car.IN_PROGRESS); // Update car cache

		this.cacheMemory.getCarCache().getCache().replace(car.getChassisNumber(), car); // Do I need this line?

		part.setCar(car);

		this.cacheMemory.getPartCache().addPart(part); // Update Part Cache

		return "[SUCCESS] The part was registered. ID: " + part.getPartId();

	}

	@Override
	public String validatePutPart(int partId, int palletId) throws RemoteException {

		// Validation

		if (!Validation.validate(partId, Validation.PART_ID))
			return "[VALIDATION ERROR] Invalid part id.";

		if (!Validation.validate(palletId, Validation.PALLET_ID))
			return "[VALIDATION ERROR] Invalid palled id.";

		if (!this.cacheMemory.getPartCache().contains(partId))
			return "[VALIDATION ERROR] Part does not exist";

		if (!this.cacheMemory.getPalletCache().contains(palletId))
			return "[VALIDATION ERROR] Pallet does not exist";

		Part part = this.cacheMemory.getPartCache().getPart(partId);
		Pallet pallet = this.cacheMemory.getPalletCache().getPallet(palletId);

		double availableWeightCapacity = pallet.getMaxWeight() - pallet.getWeight();

		if (availableWeightCapacity < part.getWeight()) // if doesn't fits on pallet
			return "[FAIL] Not enough space on the pallet";

		if (!pallet.getState().equals(Pallet.AVAILABLE))
			return "[FAIL] Pallet is not available";

		if (!pallet.getPartType().equals("-1"))
			if (!pallet.getPartType().equals(part.getType()))
				return "[FAIL] Type missmatch";

		// Update Database

		try {
			part = dataServer.executeUpdatePartPallet(part, pallet);

			if (part == null)
				return "[FAIL] 1";

			pallet.setWeight(pallet.getWeight() + part.getWeight());
			pallet = dataServer.executeUpdatePalletWeight(pallet); // TODO will this database method return an pallet object with updated weight?

			if (pallet == null)
				return "[FAIL] 2"; // TODO what if we fail here? above method was executed in database already

			pallet.getPartList().addPart(part); // TODO so far it seems useless to have PartList in Pallet object ... do we need it dor anything?

			if (pallet.getPartType().equals("-1"))
				pallet.setPartType(part.getType());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getPalletCache().getCache().replace(pallet.getPalletId(), pallet);
		this.cacheMemory.getPartCache().getCache().replace(part.getPartId(), part);

		return "[SUCCESS] Part was put on pallet";

	}

	@Override
	public String validateFinishPallet(int palletId) throws RemoteException {

		// Validation

		if (!Validation.validate(palletId, Validation.PALLET_ID))
			return "[VALIDATION ERROR] Invalid palled id.";

		if (!this.cacheMemory.getPalletCache().contains(palletId))
			return "[VALIDATION ERROR] Pallet does not exist";

		Pallet pallet = this.cacheMemory.getPalletCache().getPallet(palletId);

		if (pallet.getState().equals(Pallet.FINISHED))
			return "[FAIL] Pallet was set as finised.";

		// Update Database

		try {
			pallet = dataServer.executeSetPalletState(pallet, Pallet.FINISHED);

			if (pallet == null)
				return "[FAIL]";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getPalletCache().getCache().replace(pallet.getPalletId(), pallet);

		return "[SUCCESS] The pallet finishing was registered. ";

	}

	@Override
	public String validateFinishDismantling(String chassisNumber) throws RemoteException {

		// Validation

		if (!Validation.validate(chassisNumber, Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car chassis number. ";

		if (!this.cacheMemory.getCarCache().contains(chassisNumber)) {
			System.out.println(cacheMemory.getCarCache().toString());
			return "[VALIDATION ERROR] Car does not exist";
		}

		Car car = this.cacheMemory.getCarCache().getCar(chassisNumber);

		if (car.getState().equals(Car.FINISHED))
			return "[FAIL] Car is already finished";

		// Update Database

		try {
			car = dataServer.executeSetCarState(car, Car.FINISHED);

			if (car == null)
				return "[FAIL]";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getCarCache().getCache().replace(car.getChassisNumber(), car);

		// TODO problem - car is also many times referenced in parts cache,
		// those should be updated as well ... unless we dont use Car as field in Part
		// ...

		return "[SUCCESS] The car was set as finished.";

	}

	// THIRD CLIENT

	// TODO those are not yet refactored

	@Override
	public String validateRegisterProduct(Product product, PartList partList) throws RemoteException {
		try {
			partList.getList().forEach(x -> {
				try {
					dataServer.executeUpdatePartProduct(x, product);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			Product registeredProduct = dataServer.executeRegisterProduct(product);
			if (registeredProduct != null) {
				this.cacheMemory.getProductCache().addProduct(registeredProduct);

				partList.getList().forEach(x -> {
					Part currentPart = this.cacheMemory.getPartCache().getCache().get(x.getPartId());
					this.cacheMemory.getPartCache().getCache().replace(x.getPartId(), currentPart);
				});

				return "[SUCCESS] The product was registered. ID: " + registeredProduct.getProductId();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "[APPLICATION FAILURE] The product registration has failed. ";
	}

	@Override
	public PartCache getParts() throws RemoteException {
		return (this.cacheMemory.getPartCache() != null) ? this.cacheMemory.getPartCache() : null;
	}

	// FOURTH CLIENT

	@Override
	public PartList validateGetStolenParts(Car car) throws RemoteException {
		try {
			PartList stolenParts = new PartList();
			this.cacheMemory.getPartCache().getCache().forEach((x, y) -> {
				if (y.getCar().getChassisNumber().equals(car.getChassisNumber())) {
					stolenParts.addPart(y);
				}
			});
			if(stolenParts.count() == 0)
			{
			   return null;
			}
			return stolenParts;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public ProductList validateGetStolenProducts(Car car) throws RemoteException {
		try {
			ProductList stolenProducts = new ProductList();
			this.cacheMemory.getProductCache().getCache().forEach((x, y) -> {
				y.getPartList().getList().forEach((z) -> {
					if (z.getCar().getChassisNumber().equals(car.getChassisNumber())) {
						stolenProducts.addProduct(y);
					}
				});
			});
			if(stolenProducts.count() == 0)
			{
			   return null;
			}
			return stolenProducts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Car validateGetStolenCar(String chassisNumber) throws RemoteException {
		try {
		   Car car = cacheMemory.getCarCache().getCache().get(chassisNumber);
			return car;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// PRIVATE METHODS

	private void synchronizeServerCaches() {
		try {
			this.cacheMemory.setPartCache(this.dataServer.executeGetPartCache());
			System.out.println((this.cacheMemory.getPartCache() != null) ? "PARTS cache is now up-to-date. "
					: "A problem has occured when updating the PARTS cache. ");

			this.cacheMemory.setCarCache(this.dataServer.executeGetCarCache());
			System.out.println((this.cacheMemory.getCarCache() != null) ? "CARS cache is now up-to-date. "
					: "A problem has occured when updating the CARS cache. ");

			this.cacheMemory.setPalletCache(this.dataServer.executeGetPalletCache());
			System.out.println((this.cacheMemory.getPalletCache() != null) ? "PALLETS cache is now up-to-date. "
					: "A problem has occured when updating the PALLETS cache. ");

			this.cacheMemory.setProductCache(this.dataServer.executeGetProductCache());
			System.out.println((this.cacheMemory.getProductCache() != null) ? "PRODUCTS cache is now up-to-date. "
					: "A problem has occured when updating the PRODUCTS cache. ");

			if (this.cacheMemory.getCarCache() != null) {
				this.availableCars = new CarList();
				this.cacheMemory.getCarCache().getCache().forEach((x, y) -> {
					if (y.getState() == "In progress") {
						this.availableCars.addCar(y);
					}
				});
			}

			if (this.cacheMemory.getPalletCache() != null) {
				this.availablePallets = new PalletList();
				this.cacheMemory.getPalletCache().getCache().forEach((x, y) -> {
					if (y.getState() == "Finished") {
						this.availablePallets.addPallet(y);
					}
				});
			}
		} catch (RemoteException | SQLException e) {
			e.printStackTrace();
		}
	}

}