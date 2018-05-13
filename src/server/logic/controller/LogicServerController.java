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
import remote.interfaces.IObserver;
import server.logic.view.LogicServerView;

public class LogicServerController extends UnicastRemoteObject implements ILogicServer, IObserver {

	private static final long serialVersionUID = 1L;

	private LogicServerView view;
	private IDataServer dataServer;
	private Cache cacheMemory;

	public LogicServerController(LogicServerView view) throws RemoteException {
		this.view = view;
	}

	// Network Connection

	public void begin() throws RemoteException {
		try {
			LocateRegistry.getRegistry(1099);

			Naming.rebind("logicServer", this);

			String ip = "localhost";
			String URL = "rmi://" + ip + "/" + "dataServer";

			this.dataServer = (IDataServer) Naming.lookup(URL);
			dataServer.attach(this);

			view.show("Logic server is running... ");

			this.cacheMemory = new Cache();
			
			new Thread(new Runnable() {
			   public void run() {
      			try
               {
      			   loadCache();
               }
                  catch (RemoteException | SQLException e) {
                  view.show("Failed to load cache memory");
                  e.printStackTrace();
               }
			}
			}).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Network Methods

	// FIRST CLIENT

	@Override
	public String validateRegisterCar(Car car) throws RemoteException {

		// Validation
		
		if (car == null)
			return "[VALIDATION ERROR] Null reference passed.";

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

		return "[SUCCESS] The car was registered.";
	}

	// SECOND CLIENT

	@Override
	public CarList getAvailableCars() throws RemoteException {
		
		CarList carList = new CarList();
		
		for (Car car : cacheMemory.getCarCache().getCache().values()) 
			if (!car.getState().equals(Car.FINISHED))
				carList.addCar(car);
		

		return carList;

	}

	@Override
	public String validateRegisterPart(Part part, String chassisNumber) throws RemoteException {

		// Validate

		if (part == null)
			return "[VALIDATION ERROR] Null reference passed.";
		
		if (part.getType() == null || part.getType().isEmpty())
			return "[VALIDATION ERROR] Invalid part type.";

		if (part.getWeight() <= 0)
			return "[VALIDATION ERROR] Invalid part weight.";

		if (chassisNumber == null || chassisNumber.isEmpty())
			return "[VALIDATION ERROR] There was no car set for this part.";

		if (!Validation.validate(chassisNumber, Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car chassis number.";

		if (!cacheMemory.getCarCache().contains(chassisNumber))
			return "[VALIDATION ERROR] Car not found.";
		
		if (cacheMemory.getCarCache().getCar(chassisNumber).getState().equals(Car.FINISHED))
			return "[VALIDATION ERROR] This car is already finished.";

		
		// Update Database

		try {
			part = dataServer.executeRegisterNewPart(part, chassisNumber);
		
			if (part == null)
				return "[FAIL] Part could not be registered. ";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "[SUCCESS] The part was registered. ID: " + part.getPartId();
	}

	@Override
	public String validatePutPart(int partId, int palletId) throws RemoteException {

		// Validation

		if (!Validation.validate(partId, Validation.PART_ID))
			return "[VALIDATION ERROR] Invalid part id.";

		if (!Validation.validate(palletId, Validation.PALLET_ID))
			return "[VALIDATION ERROR] Invalid palled id.";

		if (!cacheMemory.getPartCache().contains(partId))
			return "[VALIDATION ERROR] Part not found.";
		
		if (!cacheMemory.getPalletCache().contains(palletId))
			return "[VALIDATION ERROR] Pallet not found.";
		
		
		Part part = this.cacheMemory.getPartCache().getPart(partId);
		
		Pallet pallet = this.cacheMemory.getPalletCache().getPallet(palletId);
		
		double availableWeightCapacity = pallet.getMaxWeight() - pallet.getWeight();
		
		if (availableWeightCapacity < part.getWeight())
			return "[FAIL] Not enough space on the pallet";

		if (!pallet.getState().equals(Pallet.AVAILABLE))
			return "[FAIL] Pallet is not available";

		if (!pallet.getPartType().equals("no type"))
			if (!pallet.getPartType().equals(part.getType()))
				return "[FAIL] Type missmatch";
		
	    // Check if part is already on this pallet 
	    for (Part palletsPart : pallet.getPartList().getList()) 
			if (palletsPart.getPartId() == part.getPartId())
				return "[FAIL] Pallet already contains this part.";
	    
	    // Do not allow if part is already on some pallet
	    for (Pallet palletFromCahce : cacheMemory.getPalletCache().getCache().values()) 
			for (Part partFromProduct : palletFromCahce.getPartList().getList()) 
				if (partFromProduct.getPartId() == part.getPartId()) 
					return "[FAIL] Part is already a on some pallet.";    
	    
		// Update Database

		try {
			part = dataServer.executeUpdatePartPallet(part, pallet);

			if (part == null)
				return "[FAIL] Failed database update part pallet. ";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "[SUCCESS] Part was put on pallet";
	}

	@Override
	public String validateFinishPallet(int palletId) throws RemoteException {

		// Validation

		if (!Validation.validate(palletId, Validation.PALLET_ID))
			return "[VALIDATION ERROR] Invalid palled id.";

		if (!cacheMemory.getPalletCache().contains(palletId))
			return "[VALIDATION ERROR] Pallet does not exist";

		Pallet pallet = cacheMemory.getPalletCache().getPallet(palletId);

		if (pallet.getState().equals(Pallet.FINISHED))
			return "[FAIL] Pallet was already set as finised.";

		// Update Database

		try {
			pallet = dataServer.executeUpdatePalletState(pallet, Pallet.FINISHED);

			if (pallet == null)
				return "[FAIL] Could not persist the setting of the pallet state. ";
			
			this.addNewPallets();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "[SUCCESS] The pallet was set as finished. ";

	}

	@Override
	public Pallet findAvailablePallet(int partId) throws RemoteException {
		
		if (!Validation.validate(partId, Validation.PART_ID))
			return null;

		if (!cacheMemory.getPartCache().contains(partId))
			return null;
		
		Part part = this.cacheMemory.getPartCache().getPart(partId);
 
		for (Pallet pallet : cacheMemory.getPalletCache().getCache().values())
			if (pallet.getPartType().equals(part.getType())
					&& (pallet.getWeight() + part.getWeight()) <= pallet.getMaxWeight()
					&& pallet.getState().equals(Pallet.AVAILABLE))
				return pallet;
			
		for (Pallet pallet : cacheMemory.getPalletCache().getCache().values())
         if (pallet.getPartType().equals("no type") && pallet.getPalletId() != -1)
            return pallet;
		
		return null;
	}

	@Override
	public String validateFinishDismantling(String chassisNumber) throws RemoteException {
		
		// Validation

		if (!Validation.validate(chassisNumber, Validation.CHASSIS_NUMBER))
			return "[VALIDATION ERROR] Invalid car id.";

		if (!cacheMemory.getCarCache().contains(chassisNumber))
			return "[VALIDATION ERROR] Car does not exist";

		Car car = cacheMemory.getCarCache().getCar(chassisNumber);

		if (car.getState().equals(Pallet.FINISHED))
			return "[FAIL] Car was already set as finised.";

		// Update Database

		try {
			car = dataServer.executeUpdateCarState(car, Pallet.FINISHED);

			if (car == null)
				return "[FAIL] Could not persist the setting of the car state. ";

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "[SUCCESS] The car was set as finished. ";

	}

	// THIRD CLIENT

	/*
	 * partList has only a list of parts that contain only theit Id
	 */
	@Override
	public String validateRegisterProduct(Product product) throws RemoteException {

		// Validate

		if (product == null)
			return "[VALIDATION ERROR] Null reference passed.";

		if (product.getName() == null || product.getName().isEmpty())
			return "[VALIDATION ERROR] Product name is not set.";

		if (product.getType() == null || product.getType().isEmpty())
			return "[VALIDATION ERROR] Product type is not set.";

		for (Part part : product.getPartList().getList()) {
			if (!Validation.validate(part.getPartId(), Validation.PART_ID))
				return "[VALIDATION ERROR] Invalid part Id.";
			if (!cacheMemory.getPartCache().contains(part.getPartId()))
				return "[VALIDATION ERROR] Part does not exist.";
			
			for (Product productFromCahce : cacheMemory.getProductCache().getCache().values()) 
				for (Part partFromProduct : productFromCahce.getPartList().getList()) 
					if (part.getPartId() == partFromProduct.getPartId()){
					   System.out.println(productFromCahce.getProductId());
					   System.out.println(partFromProduct.getPartId());
						return "[VALIDATION ERROR] Part is already a part of some product."; 
					}
			
			for (Pallet pallet : this.cacheMemory.getPalletCache().getCache().values()) {
				if (pallet.getPartList().contains(part) && pallet.getState().equals(Pallet.FINISHED)) {
					return "[VALIDATION ERROR] Part #" + part.getPartId() + " is currently on pallet #" + pallet.getPalletId()
							+ " that is not yet finished. ";
				}
			}
		}

		// Update Database

		try {
			product = dataServer.executeRegisterProduct(product);
			
			if (product == null)
				return "[FAIL] Failed persisting the product.";

		} catch (SQLException e) {
			e.printStackTrace();
		}


		return "[SUCCESS] The product was registered. ID: " + product.getProductId();

	}

	// FOURTH CLIENT

	@Override
	public PartList validateGetPartsByCar(String chassisNumber) throws RemoteException {
	   try {        
         Car stolenCar = cacheMemory.getCarCache().
               getCache().get(chassisNumber);
         
         if(stolenCar != null) {
            return stolenCar.getPartList();
         }
         
         return null;
      } catch (Exception e) {
         e.printStackTrace();
      }

      return null;
	}

	@Override
	public ProductList validateProductsByCar(String chassisNumber) throws RemoteException {
		try {
		   Car car = cacheMemory.getCarCache().
               getCache().get(chassisNumber);                                            // Check the cache memory if a car with given chassis number exists
         
         if(car != null)                                                                 // If the car is in the cache memory
         {
            final ProductList productsFromCache = new ProductList();                     // Create empty final list of stolen products
            PartList parts = car.getPartList();                                          // Get all the parts from the stolen car
            cacheMemory.getProductCache().getCache().forEach((productId, product) -> {   // For each entry in product cache
               product.getPartList().getList().forEach((part) -> {                       // For each entry in part list of the product
                  if(parts.contains(part)) {                                             // Find matching parts and products
                     productsFromCache.addProduct(product);                              // Add stolen products into the list
                  }
               });
            });
            return productsFromCache;
         }
         return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Car validateGetCar(String chassisNumber) throws RemoteException {
		try {
			Car car = cacheMemory.getCarCache().getCache().get(chassisNumber);
			
         return car;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

   @Override
   public <T> void update(Transaction<T> t)
   {
      cacheMemory.updateCache(t);
   }
   
   private void addNewPallets() {
		int availablePallets = 0;
		
		for (Pallet pallet : this.cacheMemory.getPalletCache().getCache().values()) {
			if (pallet.getState().equals(Pallet.AVAILABLE)) {
				++availablePallets;
			}
		}
		
		if (availablePallets < 20) {
			try {
				PalletList pallets = new PalletList();
				
				for (int i = 0; i < 500; i++) {
					Pallet pallet = new Pallet();
					pallet.setWeight(0);
					pallet.setPartType("no type");
					pallet.setMaxWeight(2100);
					pallet.setState(Pallet.AVAILABLE);
					pallets.addPallet(pallet);
				}
				
				int addedPalletsCount = this.dataServer.executeRegisterNewPallets(pallets);
				
				if (addedPalletsCount != 0) {
					this.view.show("[SUCCESS] A total of " + addedPalletsCount + " pallets was added. ");
				}
			} catch (RemoteException | SQLException e) {
				e.printStackTrace();
			}
		}
   }
   
   private void loadCache() throws RemoteException, SQLException {

		// Getting lists from data server
		
		PartList partList = dataServer.executeGetAllParts();
		
		if (partList == null) {
			view.show("A problem has occured when updating PART list.");
			return;
		}
		
		CarList carList = dataServer.executeGetAllCars();
		
		if (carList == null) {
			view.show("A problem has occured when updating CAR list.");
			return;
		}
		
		PalletList palletList = dataServer.executeGetAllPallets();
		
		if (palletList == null) {
			view.show("A problem has occured when updating PALLET list.");
			return;
		}
		
		ProductList productList = dataServer.executeGetAllProducts();
		
		if (productList == null) {
			view.show("A problem has occured when updating PRODUCT list.");
			return;
		}

		// Populating Cache	
		
		partList.getList().forEach((part) -> {
		   cacheMemory.getPartCache().addPart(part);
		});
		
		carList.getList().forEach((car) -> {
		   Car newCar = new Car(car.getChassisNumber(), car.getManufacturer(), 
              car.getModel(), car.getYear(), car.getWeight(), car.getState());        
        
        cacheMemory.getCarCache().addCar(newCar);
        
        car.getPartList().getList().forEach((part) -> {
           newCar.getPartList().addPart(cacheMemory.getPartCache().getPart(part.getPartId()));
        });
		});
		
		for (Pallet pallet: palletList.getList()) {
			Pallet newPallet = new Pallet(pallet.getPalletId(), pallet.getMaxWeight(), pallet.getState());
			newPallet.setPartType(pallet.getPartType());
			newPallet.setWeight(pallet.getWeight());
			
			cacheMemory.getPalletCache().addPallet(newPallet);
			
			for (Part part : pallet.getPartList().getList()) 
				newPallet.getPartList().addPart(cacheMemory.getPartCache().getPart(part.getPartId()));
		}
		
		for (Product product: productList.getList()) {
			Product newProduct= new Product(product.getProductId(),product.getType(), product.getName());
			
			cacheMemory.getProductCache().addProduct(newProduct);
			
			for (Part part : product.getPartList().getList()) 
				newProduct.getPartList().addPart(cacheMemory.getPartCache().getPart(part.getPartId()));
		}
		
		view.show("Cache loaded.");
	}
}