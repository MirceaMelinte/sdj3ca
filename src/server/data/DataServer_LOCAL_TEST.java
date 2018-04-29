package server.data;

import remote.interfaces.IDataServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.*;
import model.cache.*;

public class DataServer_LOCAL_TEST extends UnicastRemoteObject implements IDataServer {
	private static final long serialVersionUID = 1L;
	private static Connection connection;

	public DataServer_LOCAL_TEST() throws RemoteException {
		super();

	}

	public void begin() {
		try {
			LocateRegistry.createRegistry(1099);

			Naming.rebind("dataServer", this);

			System.out.println("Data server is running... ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public Car executeRegisterCar(Car car) throws SQLException, RemoteException {

		return new Car();
	}

	@Override
	public Pallet executeRegisterPallet(Pallet pallet) throws RemoteException, SQLException {

		return null;
	}

	@Override
	public Product executeRegisterProduct(Product product) throws RemoteException, SQLException {

		return null;
	}

	@Override
	public Part executeRegisterNewPart(Part part) throws RemoteException, SQLException {
		part.setPartId(12341234);
		return part;
	}



	@Override
	public Part executeUpdatePartProduct(Part part, Product product) throws RemoteException, SQLException {
		
		return null;
	}



	@Override
	public PartCache executeGetPartCache() throws RemoteException, SQLException {
		PartCache pc = new PartCache();
		Part p1 =  new Part(11110000, "wheel", 3, new Car("1234567c", "porshe", "300", 2001, 2400, Car.IN_PROGRESS));
		p1.setPalletId(2222);
		Part p2 =  new Part(11110001, "wheel", 3, new Car("1234567f", "ferrary", "600", 2004, 1900, Car.FINISHED));
		p2.setPalletId(2222);
		
		pc.addPart(p1);
		pc.addPart(p2);
		
		return pc;

	}

	@Override
	public CarCache executeGetCarCache() throws RemoteException, SQLException {

		CarCache cc = new CarCache();
		Car c1 = new Car("1234567a", "bmw", "100", 2011, 3500, Car.AVAILABLE);
		Car c2 = new Car("1234567b", "mercedes", "200", 2014, 3400, Car.AVAILABLE);
		Car c3 = new Car("1234567c", "porshe", "300", 2001, 2400, Car.IN_PROGRESS);
		Car c4 = new Car("1234567d", "honda", "400", 2004, 3400, Car.IN_PROGRESS);
		Car c5 = new Car("1234567e", "fiat", "500", 2004, 3500, Car.FINISHED);
		Car c6 = new Car("1234567f", "ferrary", "600", 2004, 1900, Car.FINISHED);
		cc.addCar(c1);
		cc.addCar(c2);
		cc.addCar(c3);
		cc.addCar(c4);
		cc.addCar(c5);
		cc.addCar(c6);
		return cc;
		
	}

	@Override
	public PalletCache executeGetPalletCache() throws RemoteException, SQLException {
		PalletCache pc = new PalletCache();
		Pallet p1 = new Pallet(2222, 1000, Pallet.AVAILABLE);
		p1.setPartType("wheel");
		Pallet p2 = new Pallet(3333, 1000, Pallet.AVAILABLE);
		p2.setPartType("hood");
		Pallet p3 = new Pallet(4444, 1000, Pallet.AVAILABLE);
		p3.setPartType("-1");
		
		pc.addPallet(p1);
		pc.addPallet(p2);
		pc.addPallet(p3);
		return pc;

	}

	@Override
	public ProductCache executeGetProductCache() throws RemoteException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	public static void main(String[] args) throws RemoteException {
		DataServer_LOCAL_TEST d = new DataServer_LOCAL_TEST();

		d.begin();
	}

	@Override
	public Pallet executeSetPalletState(Pallet pallet, String state) throws RemoteException, SQLException {
		pallet.setState(state);
		return pallet;
	}

	@Override
	public Car executeSetCarState(Car car, String state) throws RemoteException, SQLException {
		car.setState(state);
		return car;
	}


	@Override
	public Part executeUpdatePartPallet(Part part, Pallet pallet) throws RemoteException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pallet executeUpdatePalletWeight(Pallet pallet) throws RemoteException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}