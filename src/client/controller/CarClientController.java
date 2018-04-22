package client.controller;

import java.rmi.Naming;
import java.rmi.RemoteException;

import client.view.CarClientView;
import interfaces.ILogicServer;
import model.Car;

public class CarClientController {

	private CarClientView view;
	private ILogicServer logicServer;

	public CarClientController(CarClientView view) throws RemoteException {
		this.view = view;

	}

	public void execute(String what) throws RemoteException {
		switch (what) {
		case "Add Car":
			Car car = new Car();
			car.setManufacturer(view.get("Manufacturer"));
			car.setModel(view.get("Model"));
			car.setYear(Integer.parseInt(view.get("Year")));
			car.setWeight(Double.parseDouble(view.get("Weight")));
			car.setChassisNumber(view.get("Chassis Number"));
			registerCar(car);
			break;
		case "Quit":
			System.exit(0);
			break;
		default:
			break;
		}
	}

	// Network Connection
	public void begin() {
		try {
			String ip = "localhost";
			String URL = "rmi://" + ip + "/" + "logicServer";

			logicServer = (ILogicServer) Naming.lookup(URL);

			view.show("Car Client connection established");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Network Methods
	public void registerCar(Car car) throws RemoteException {
		view.show("[Server Response] " + logicServer.validateRegisterCar(car));
	}
}
