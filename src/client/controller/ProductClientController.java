package client.controller;

import java.rmi.Naming;
import java.rmi.RemoteException;

import remote.interfaces.ILogicServer;
import client.view.PartClientView;
import client.view.ProductClientView;
import model.Car;
import model.Pallet;
import model.Part;
import model.PartList;
import model.Product;

public class ProductClientController {

	private ProductClientView view;
	private ILogicServer logicServer;

	public ProductClientController(ProductClientView view) throws RemoteException {
		this.view = view;
	}

	public void execute(String what) throws RemoteException {
		switch (what) {
		case "Add Product":
			Product product = new Product();
			product.setName(view.get("Name"));
			product.setType(view.get("Type"));
			PartList partList = new PartList();
			while (true) {
				String input = view.get("Part ID (enter '0' to exit)");
				if (input.equals("0")) break;
				
				Part part = new Part();
				part.setPartId(Integer.parseInt(input));
				partList.addPart(part);
			}
			registerProduct(product, partList);
			break;
		case "List Parts":
			listParts();
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
	public void registerProduct(Product product, PartList partList) throws RemoteException {
		view.show("[Server Response] " + logicServer.validateRegisterProduct(product, partList));
	}

	public void listParts() throws RemoteException {
		view.show(logicServer.getParts().toString());
	}

}
