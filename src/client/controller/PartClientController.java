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
			Part part = new Part();
			part.setName(view.get("Name"));
			part.setType(view.get("Type"));
			part.setWeight(Double.parseDouble(view.get("Weight")));

			Car car = new Car();
			car.setChassisNumber(view.get("Car Chassis Number"));

			Pallet pallet = new Pallet();
			pallet.setPalletId(Integer.parseInt(view.get("Pallet ID")));

			part.setCar(car);
			part.setPallet(pallet);
			registerPart(part);
			break;
		case "Add Pallet":
			Pallet pallet1 = new Pallet();
			pallet1.setPartType(view.get("Part Type"));
			pallet1.setMaxWeight(Double.parseDouble(view.get("Max Weight")));

			registerPallet(pallet1);
			break;
		case "Set Pallet As Finished":
			Pallet pallet2 = new Pallet();
			pallet2.setPalletId(Integer.parseInt(view.get("Pallet ID")));
			
			finishPallet(pallet2);
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
	
	public void finishPallet(Pallet pallet) throws RemoteException {
		view.show("[Server Response] " + logicServer.validateFinishPallet(pallet));
	}
}
