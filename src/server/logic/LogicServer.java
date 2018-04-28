package server.logic;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Calendar;

import remote.interfaces.IDataServer;
import remote.interfaces.ILogicServer;
import model.Car;
import model.CarList;
import model.Pallet;
import model.PalletList;
import model.Part;
import model.PartList;
import model.Product;
import model.ProductList;
import model.cache.Cache;
import model.cache.PartCache;

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
	
	@Override
	public String validateFinishDismantling(Car car) throws RemoteException {
		try {
			if (car.getChassisNumber().length() <= 17) {
				if (car.getState().equals("In progress")) {
					if (car.getYear() <= Calendar.getInstance().get(Calendar.YEAR)) {
						if (dataServer.executeSetCarState(car, "Finished") != null) {
							Car currentCar = this.cacheMemory.getCarCache().getCache().get(car.getChassisNumber());
							this.cacheMemory.getCarCache().getCache().replace(currentCar.getChassisNumber(), currentCar);
							
							this.availableCars.removeCar(car.getChassisNumber());
							
							return "[SUCCESS] The car dismantling was registered. ";
						}
					} return "[VALIDATION ERROR] Invalid car registration year. ";
				} return "[VALIDATION ERROR] Invalid car state. ";
			} return "[VALIDATION ERROR] Invalid car chassis number. ";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "[APPLICATION FAILURE] The car dismantling registration has failed. ";
	}
	
	@Override
	public String validateFinishPallet(Pallet pallet) throws RemoteException {
		try {
			if (pallet.getState().equals("Available")) {
				if (dataServer.executeSetPalletState(pallet, "Finished") != null) {
					Pallet currentPallet = this.cacheMemory.getPalletCache().getCache().get(pallet.getPalletId());
					this.cacheMemory.getPalletCache().getCache().replace(currentPallet.getPalletId(), currentPallet);
					
					this.availablePallets.removePallet(pallet.getPalletId());
					
					return "[SUCCESS] The pallet finishing was registered. ";
				}
			} return "[VALIDATION ERROR] Invalid pallet state. ";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "[APPLICATION FAILURE] The pallet finishing registration has failed. ";
	}

	@Override
	public String validateRegisterCar(Car car) throws RemoteException {
		try {
			if (car.getChassisNumber().length() <= 17) {
				if (car.getState().equals("Finished") || car.getState().equals("In progress")) {
					if (car.getYear() <= Calendar.getInstance().get(Calendar.YEAR)) {
						if (dataServer.executeRegisterCar(car) != null) {
							this.cacheMemory.getCarCache().addCar(car);
							
							if (car.getState().equals("Available")) {
								this.availableCars.addCar(car);
							}
							
							return "[SUCCESS] The car was registered. ";
						}
					} return "[VALIDATION ERROR] Invalid car registration year. ";
				} return "[VALIDATION ERROR] Invalid car state. ";
			} return "[VALIDATION ERROR] Invalid car chassis number. ";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "[APPLICATION FAILURE] The car registration has failed. ";
	}

	@Override
	public PartList validateGetStolenParts(Car car) throws RemoteException {
		try {
			PartList stolenParts = new PartList();
			this.cacheMemory.getPartCache().getCache().forEach((x, y) -> {
				if (y.getCar().getChassisNumber().equals(car.getChassisNumber())) {
					stolenParts.addPart(y);
				}
			});
			
			return stolenParts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
   @Override
   public ProductList validateGetStolenProducts(Car car) throws RemoteException
   {
      try {
         ProductList stolenProducts = new ProductList();
         this.cacheMemory.getProductCache().getCache().forEach((x, y) -> {    
            y.getPartList().getList().forEach((z) -> {
               if(z.getCar().getChassisNumber().equals(car.getChassisNumber())){
                  stolenProducts.addProduct(y);
               }
            });
         });
         return stolenProducts;
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      return null;
   }

   @Override
   public Car validateGetStolenCar(String chassisNumber) throws RemoteException
   {
      try {
         
         return cacheMemory.getCarCache().getCache().get(chassisNumber);
         
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      return null;
   }

	@Override
	public String validateRegisterPart(Part part) throws RemoteException {
		try {
			if (part.getCar() != null) {
				if (part.getCar().getChassisNumber().length() <= 17) {
					if (part.getCar().getState().equals("In progress")) {
						if (part.getCar().getYear() <= Calendar.getInstance().get(Calendar.YEAR)) {
							Part registeredPart = dataServer.executeRegisterNewPart(part);
							if (registeredPart != null) {
								this.cacheMemory.getPartCache().addPart(registeredPart);
								
								return "[SUCCESS] The part was registered. ID: " + registeredPart.getPartId();
							}
						} return "[VALIDATION ERROR] Invalid car registration year. ";
					} return "[VALIDATION ERROR] Invalid car state. ";
				} return "[VALIDATION ERROR] Invalid car chassis number. ";
			} return "[VALIDATION ERROR] There was no car set for this part. ";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "[APPLICATION FAILURE] The part registration has failed. ";
	}

	@Override
	public String validateRegisterPallet(Pallet pallet) throws RemoteException {
		try {
			if (pallet.getState().equals("Finished") || pallet.getState().equals("Available")) {
				Pallet registeredPallet = dataServer.executeRegisterPallet(pallet);
				if (registeredPallet != null) {
					this.cacheMemory.getPalletCache().addPallet(registeredPallet);
					
					if (registeredPallet.getState().equals("Finished")) {
						this.availablePallets.addPallet(registeredPallet);
					}
					
					return "[SUCCESS] The pallet was registered. ID: " + registeredPallet.getPalletId();
				}
			} return "[VALIDATION ERROR] Invalid pallet state. ";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "[APPLICATION FAILURE] The pallet registration has failed. ";
	}
	
	@Override
	public String validateSetPalletWeight(Pallet pallet, double newWeight) throws RemoteException {
		try {
			if (pallet.getState().equals("Available")) {
				if (newWeight <= pallet.getMaxWeight()) {
					pallet.setWeight(newWeight);
					Pallet updatedPallet = dataServer.executeUpdatePalletWeight(pallet);
					if (updatedPallet != null) {
						this.cacheMemory.getPalletCache().getCache().replace(pallet.getPalletId(), updatedPallet);
						
						if (updatedPallet.getState() != "Finished") {
							this.availablePallets.removePallet(pallet.getPalletId());
							this.availablePallets.addPallet(updatedPallet);
						}
						
						return "[SUCCESS] The pallet was updated. ID: " + updatedPallet.getPalletId();
					}
				} return "[VALIDATION ERROR] Cannot put more items on this pallet. ";
			} return "[VALIDATION ERROR] Invalid pallet state. ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "[APPLICATION FAILURE] The pallet weight setting has failed. ";
	}

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
		return (this.cacheMemory.getPartCache() != null)
				? this.cacheMemory.getPartCache()
				: null;
	}
	
	private void synchronizeServerCaches() {
		try {
			this.cacheMemory.setPartCache(this.dataServer.executeGetPartCache());
			System.out.println((this.cacheMemory.getPartCache() != null) 
								? "PARTS cache is now up-to-date. "
								: "A problem has occured when updating the PARTS cache. ");
			
			this.cacheMemory.setCarCache(this.dataServer.executeGetCarCache());
			System.out.println((this.cacheMemory.getCarCache() != null) 
								? "CARS cache is now up-to-date. "
								: "A problem has occured when updating the CARS cache. ");

			this.cacheMemory.setPalletCache(this.dataServer.executeGetPalletCache());
			System.out.println((this.cacheMemory.getPalletCache() != null) 
								? "PALLETS cache is now up-to-date. "
								: "A problem has occured when updating the PALLETS cache. ");

			this.cacheMemory.setProductCache(this.dataServer.executeGetProductCache());
			System.out.println((this.cacheMemory.getProductCache() != null) 
								? "PRODUCTS cache is now up-to-date. "
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