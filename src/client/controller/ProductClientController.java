package client.controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.Part;
import model.PartList;
import model.Product;
import remote.interfaces.ILogicServer;

public class ProductClientController {

	private ILogicServer logicServer;
	private PartList partList = new PartList();

	public ProductClientController() throws RemoteException, MalformedURLException, NotBoundException {
		logicServer = (ILogicServer) Naming.lookup("rmi://localhost/logicServer");
	}

	@FXML
	private Button btnClearListOfParts;

	@FXML
	private ListView<String> lvListOfParts = new ListView<String>();

	@FXML
	private TextField tfPartId;

	@FXML
	private Button btnAddPart;

	@FXML
	private Button btnRegisterProduct;

	@FXML
	private TextField tfProductName;

	@FXML
	private TextField tfProductType;

	@FXML
	void onAddPart(ActionEvent event) {
		try {
			int partId = Integer.parseInt(tfPartId.getText());
			Part part = new Part();
			part.setPartId(partId);
			partList.addPart(part);
			rerenderPartList();
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Invalid Input");
			alert.showAndWait();
		}
	}

	@FXML
	void onSubmitProduct(ActionEvent event) throws RemoteException {
		Product product = new Product();
		product.setName(tfProductName.getText());
		product.setType(tfProductType.getText());
		
		String serverResponse = logicServer.validateRegisterProduct(product, partList);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Server Response");
		alert.setHeaderText(null);
		alert.setContentText(serverResponse);
		alert.showAndWait();
	}

	@FXML
	void onClear(ActionEvent event) {
		partList = new PartList();
		rerenderPartList();
	}

	private void rerenderPartList() {
		ObservableList<String> items = FXCollections.observableArrayList();

		for (Part part : partList.getList()) {
			items.add(Integer.toString(part.getPartId()));
		}

		lvListOfParts.setItems(items);
	}
}

