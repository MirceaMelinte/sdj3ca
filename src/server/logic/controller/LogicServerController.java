package server.logic.controller;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import common.Validation;
import model.*;
import model.cache.*;
import remote.interfaces.IDataServer;
import remote.interfaces.ILogicServer;
import server.logic.view.LogicServerView;

public class LogicServerController extends UnicastRemoteObject implements ILogicServer {

	private static final long serialVersionUID = 1L;

	private LogicServerView view;

	public LogicServerController(LogicServerView view) throws RemoteException {
		this.view = view;
	}

	private IDataServer dataServer;
	
	private Cache cacheMemory;

	// Network Connection

	public void begin() throws RemoteException {
		try {
			LocateRegistry.getRegistry(1099);

			Naming.rebind("logicServer", this);

			String ip = "localhost";
			String URL = "rmi://" + ip + "/" + "dataServer";

			this.dataServer = (IDataServer) Naming.lookup(URL);

			view.show("Logic server is running... ");

			this.cacheMemory = new Cache();
			System.out.println("Caches initialized. ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Network Methods

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

	@Override
	public CarList getAvailableCars() throws RemoteException {

		CarList carList = this.dataServer.getAvailableCars();
		
		if (carList != null) {
			carList.getList().forEach(x -> {
				this.cacheMemory.getCarCache().addCar(x);
			});
		}

		return carList;

	}

	@Override
	public String validateRegisterPart(Part part) throws RemoteException {
		Car car = null;
		
		for (Car cachedCar : this.cacheMemory.getCarCache().getCache().values()) {
			if (cachedCar.getPartList().contains(part)) {
				car = cachedCar;
			}
		}
		
		if (car == null) {
			this.dataServer.getCarByPart(part.getPartId());
		}
		
		// Validate

		if (part.getType() == null || part.getType().isEmpty())
			return "[VALIDATION ERROR] Invalid part type.";

		if (part.getWeight() <= 0)
			return "[VALIDATION ERROR] Invalid part weight.";
		
		if (car == null)
			return "[VALIDATION ERROR] Car could not be retrieved. ";
		
		if (car.getChassisNumber() == null || car.getChassisNumber().isEmpty())
			return "[VALIDATION ERROR] There was no car set for this part.";

		if (!Validation.validate(car.getChassisNumber(), Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car chassis number.";

		if (!this.cacheMemory.getCarCache().contains(car.getChassisNumber()))
			return "[VALIDATION ERROR] This car does not exist.";

		if (this.cacheMemory.getCarCache().getCar(car.getChassisNumber()).getState().equals(Car.FINISHED))
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
		
		car.getPartList().addPart(part);
		car.setState(Car.IN_PROGRESS);
		this.cacheMemory.getCarCache().addCar(car);

		return "[SUCCESS] The part was registered. ID: " + part.getPartId();
	}

	@Override
	public String validatePutPart(int partId, int palletId) throws RemoteException {

		// Validation

		if (!Validation.validate(partId, Validation.PART_ID))
			return "[VALIDATION ERROR] Invalid part id.";

		if (!Validation.validate(palletId, Validation.PALLET_ID))
			return "[VALIDATION ERROR] Invalid palled id.";

		Part part = !(this.cacheMemory.getPartCache().contains(partId))
					? this.dataServer.getPartById(partId)
					: this.cacheMemory.getPartCache().getPart(partId);
		
		Pallet pallet = !(this.cacheMemory.getPalletCache().contains(palletId))
						? this.dataServer.getPalletById(palletId)
						: this.cacheMemory.getPalletCache().getPallet(palletId);

		double availableWeightCapacity = pallet.getMaxWeight() - pallet.getWeight();

		if (availableWeightCapacity < part.getWeight())
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
				return "[FAIL] Failed database update part pallet. ";

			pallet.setWeight(pallet.getWeight() + part.getWeight());
			pallet = dataServer.executeUpdatePalletWeight(pallet);
			
			if (pallet == null)
				return "[FAIL] Failed database update pallet weight. ";

			pallet.getPartList().addPart(part);

			if (pallet.getPartType().equals("-1"))
				pallet.setPartType(part.getType());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getPalletCache().addPallet(pallet);
		this.cacheMemory.getPartCache().addPart(part);

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
				return "[FAIL] Could not persist the setting of the pallet state. ";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getPalletCache().addPallet(pallet);

		return "[SUCCESS] The pallet finishing was registered. ";

	}

	// TODO not yet used in client

	@Override
	public Pallet findAvailablePallet(Part part) throws RemoteException {
		if (!Validation.validate(part.getPartId(), Validation.PART_ID))
			return null;

		if (!this.cacheMemory.getPartCache().contains(part.getPartId()))
			return null;
		
		// TODO: Retrieve from DB as well. 
		
		for (Pallet pallet : this.cacheMemory.getPalletCache().getCache().values()) {
			if (pallet.getPartType().equals(part.getType())
					&& (pallet.getWeight() + part.getWeight()) <= pallet.getMaxWeight()) {
				return pallet;
			}
		}

		return null;
	}

	@Override
	public String validateFinishDismantling(String chassisNumber) throws RemoteException {

		// Validation

		if (!Validation.validate(chassisNumber, Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car chassis number. ";

		if (!this.cacheMemory.getCarCache().contains(chassisNumber)) {
			view.show(cacheMemory.getCarCache().toString());
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

	/*
	 * partList has only a list of parts that contain only theit Id
	 */
	@Override
	public String validateRegisterProduct(Product product, PartList partList) throws RemoteException {

		// Validate

		if (product == null || partList == null)
			return "[VALIDATION ERROR] Null objects.";

		if (product.getName() == null || product.getName().isEmpty())
			return "[VALIDATION ERROR] Product name is not set.";

		if (product.getType() == null || product.getType().isEmpty())
			return "[VALIDATION ERROR] Product type is not set.";

		for (Part part : partList.getList()) {
			if (!Validation.validate(part.getPartId(), Validation.PART_ID))
				return "[VALIDATION ERROR] Invalid part Id.";
			if (!this.cacheMemory.getPartCache().contains(part.getPartId()))
				return "[VALIDATION ERROR] Part does not exist.";

		}

		PartList fullPartList = this.dataServer.getPartsByProduct(product.getProductId());
		fullPartList.getList().forEach(x -> {
			this.cacheMemory.getPartCache().addPart(x);
		});

		// TODO check if part is already a part of a product

		// Update Database

		try {
			product = dataServer.executeRegisterProduct(product);
			
			if (product == null)
				return "[FAIL] Fail in the database query. ";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Update Cache

		this.cacheMemory.getProductCache().addProduct(product);

		return "[SUCCESS] The product was registered. ID: " + product.getProductId();

	}

	// FOURTH CLIENT

	@Override
	public PartList validateGetStolenParts(Car car) throws RemoteException {
	   try {        
         Car stolenCar = cacheMemory.getCarCache().
               getCache().get(car.getChassisNumber());
         
         if(stolenCar != null)
         {
            return stolenCar.getPartList();
         }
         else
         {
            PartList partList = dataServer.executeGetStolenParts(car);
            if(partList != null)
            {
               partList.getList().forEach((x) -> 
               cacheMemory.getPartCache().getCache().put(x.getPartId(), x));
            } 
            return partList;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return null;
	}

	@Override
	public ProductList validateGetStolenProducts(Car car) throws RemoteException {
		try {
		   
		   PartList stolenParts = cacheMemory.getCarCache().
               getCache().get(car.getChassisNumber()).getPartList();
		   
		   ProductList stolenProducts = new ProductList();
		   
			this.cacheMemory.getProductCache().getCache().forEach((x, y) -> {
				y.getPartList().getList().forEach((z) -> {
				   if(stolenParts.contains(z)) {
		               stolenProducts.addProduct(y);
				   }
				});
			});
			if (stolenProducts.count() == 0) {
				return null;
			}
			return stolenProducts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	  @Override
	   public PalletList validateGetStolenPallets(Car car) throws RemoteException
	   {
	     try {
	         
	         PartList stolenParts = cacheMemory.getCarCache().
	               getCache().get(car.getChassisNumber()).getPartList();
	         
	         PalletList stolenPallets = new PalletList();
	         
	         this.cacheMemory.getPalletCache().getCache().forEach((x, y) -> {
	            y.getPartList().getList().forEach((z) -> {
	               if(stolenParts.contains(z)) {
	                  stolenPallets.addPallet(y);
	               }
	            });
	         });
	         if (stolenPallets.count() == 0) {
	            return null;
	         }
	         return stolenPallets;
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return null;
	   }

	@Override
	public Car validateGetStolenCar(String chassisNumber) throws RemoteException {
		try {
			Car car = cacheMemory.getCarCache().getCache().get(chassisNumber);
			
			if(car == null)
			{
			   car = dataServer.executeGetStolenCar(chassisNumber);
			   if(car != null)
			   {
			      cacheMemory.getCarCache().getCache().put(car.getChassisNumber(), car);
			   }
			}
			return car;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
