package client.controller;

import java.rmi.Naming;
import java.rmi.RemoteException;

import client.view.PartClientView;
import interfaces.ILogicServer;
import model.Car;
import model.Pallet;
import model.Part;

public class PartClientController {

	private PartClientView view;
	private ILogicServer logicServer;

	public PartClientController(PartClientView view) throws RemoteException {
		this.view = view;
	}

	public void execute(String what) throws RemoteException {
		switch (what) {
		case "Add Part":
		   int partId = 1; // Dummy value for test only
		   String type = view.get("Type");
		   double weight = Double.parseDouble(view.get("Weight"));
		   int palletId = Integer.parseInt(view.get("Pallet ID"));
		   String chassisNumber = view.get("Chassis Number");
		   Car car = new Car();
		   car.setChassisNumber(chassisNumber);
		   
			Part part = new Part(partId, type, weight, car);
			part.setPalletId(palletId);

			registerPart(part);
			break;
		case "Add Pallet":
			Pallet pallet1 = new Pallet();
			pallet1.setPartType(view.get("Part Type"));
			pallet1.setWeight(Double.parseDouble(view.get("Weight")));
			pallet1.setMaxWeight(Double.parseDouble(view.get("Max Weight")));
			pallet1.setState(view.get("State"));

			registerPallet(pallet1);
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

			view.show("Part Client connection established");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Network Methods
	public void registerPart(Part part) throws RemoteException {
		view.show("[Server Response] " + logicServer.validateRegisterPart(part));
	}

	public void registerPallet(Pallet pallet) throws RemoteException {
		view.show("[Server Response] " + logicServer.validateRegisterPallet(pallet));
	}
}
