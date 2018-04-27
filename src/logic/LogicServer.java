package logic;

import interfaces.IDataServer;
import interfaces.ILogicServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import model.Car;
import model.CarList;
import model.Pallet;
import model.PalletList;
import model.Part;
import model.PartCache;
import model.PartList;
import model.Product;

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
			LocateRegistry.getRegistry(1099); // Registry is created on dataServer, here we just get it

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
			if (dataServer.executeFinishCar(car) != null) {
				Car currentCar = this.cacheMemory.getCarCache().getCache().get(car.getChassisNumber());
				this.cacheMemory.getCarCache().getCache().replace(currentCar.getChassisNumber(), currentCar);
				
				this.availableCars.removeCar(car.getChassisNumber());
				
				return "The car dismantling was registered";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "The car dismantling registration has failed";
	}
	
	@Override
	public String validateFinishPallet(Pallet pallet) throws RemoteException {
		try {
			if (dataServer.executeFinishPallet(pallet) != null) {
				Pallet currentPallet = this.cacheMemory.getPalletCache().getCache().get(pallet.getPalletId());
				this.cacheMemory.getPalletCache().getCache().replace(currentPallet.getPalletId(), currentPallet);
				
				this.availablePallets.removePallet(pallet.getPalletId());
				
				return "The pallet finishing was registered";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "The pallet finishing registration has failed";
	}

	@Override
	public String validateRegisterCar(Car car) throws RemoteException {
		try {
			if (dataServer.executeRegisterCar(car) != null) {
				this.cacheMemory.getCarCache().addCar(car);
				
				if (car.getState().equals("In progress")) {
					this.availableCars.addCar(car);
				}
				
				return "The car was registered";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "The car registration has failed";
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
	public String validateRegisterPart(Part part) throws RemoteException {
		try {
			Part registeredPart = dataServer.executeRegisterNewPart(part);
			if (registeredPart != null) {
				this.cacheMemory.getPartCache().addPart(registeredPart);
				
				return "The part was registered. id: " + registeredPart.getPartId();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "The part registration has failed";
	}

	@Override
	public String validateRegisterPallet(Pallet pallet) throws RemoteException {
		try {
			Pallet registeredPallet = dataServer.executeRegisterPallet(pallet);
			if (registeredPallet != null) {
				this.cacheMemory.getPalletCache().addPallet(registeredPallet);
				
				if (registeredPallet.getState().equals("Finished")) {
					this.availablePallets.addPallet(registeredPallet);
				}
				
				return "The pallet was registered. id: " + registeredPallet.getPalletId();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "The pallet registration has failed";
	}

	@Override
	public String validateRegisterProduct(Product product, PartList partList) throws RemoteException {
		try {
			for (Part part : partList.getList()) {
				dataServer.executeUpdatePartProduct(part, product);
			}
			
			Product registeredProduct = dataServer.executeRegisterProduct(product);
			if (registeredProduct != null) {
				this.cacheMemory.getProductCache().addProduct(registeredProduct);
				
				partList.getList().forEach(x -> {					
					Part currentPart = this.cacheMemory.getPartCache().getCache().get(x.getPartId());				
					this.cacheMemory.getPartCache().getCache().replace(x.getPartId(), currentPart);
				});
				
				return "The product was registered. id: " + registeredProduct.getProductId();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "The product registration has failed";
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