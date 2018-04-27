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

			dataServer = (IDataServer) Naming.lookup(URL);

			System.out.println("Logic server is running... ");

			cacheMemory = new Cache();
			cacheMemory.setPartCache(this.dataServer.executeGetParts());

			System.out.println((cacheMemory.getCarCache() != null) ? "partListCache is now up-to-date. "
					: "A problem has occured when updating the partListCache. ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RemoteException {
		LogicServer l = new LogicServer();
		l.begin();
	}

	@Override
	public String validateRegisterCar(Car car) throws RemoteException {
		try {
			if (dataServer.executeRegisterCar(car) != null) {
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
			return dataServer.executeGetStolenParts(car);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String validateRegisterPart(Part part) throws RemoteException {
		try {
			Part registeredPart = dataServer.executeRegisterNewPart(part);
			if (registeredPart != null)
				return "The part was registered. id: " + registeredPart.getPartId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "The part registration has failed";
	}

	@Override
	public String validateRegisterPallet(Pallet pallet) throws RemoteException {
		try {
			Pallet registeredPallet = dataServer.executeRegisterPallet(pallet);
			if (registeredPallet != null)
				return "The pallet was registered. id: " + registeredPallet.getPalletId();
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
			if (registeredProduct != null)
				return "The product was registered. id: " + registeredProduct.getProductId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "The product registration has failed";
	}

	@Override
	public PartCache getParts() throws RemoteException {
		try {
			return dataServer.executeGetParts();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
