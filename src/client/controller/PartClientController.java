package client.controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import model.Car;
import model.CarList;
import model.Pallet;
import model.Part;
import remote.interfaces.ILogicServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class PartClientController {
	private ILogicServer logicServer;
	private CarList cl;
	private Car selected;

	public PartClientController() throws RemoteException, MalformedURLException, NotBoundException {
		logicServer = (ILogicServer) Naming.lookup("rmi://localhost/logicServer");
	}

	@FXML
	private ListView<String> lvAvailableCars = new ListView<String>();

	@FXML
	private TextField tfRegisterPart_type;

	@FXML
	private TextField tfPutPart_partId;

	@FXML
	private Button btnPutPart;

	@FXML
	private Button btnFinishCar;

	@FXML
	private Button btnSelectCar;

	@FXML
	private Button btnFinishPallet;

	@FXML
	private TextField tfRegisterPart_weight;

	@FXML
	private TextField tfFinishPallet_palletId;

	@FXML
	private TextField tfFinishCar_chassisNumber;

	@FXML
	private Button btnRegisterPart;

	@FXML
	private TextField tfRegisterPart_chassisNumber = new TextField();

	@FXML
	private TextField tfPutPart_palletId;

	@FXML
	private Button btnRefreshAvailableCars;

	@FXML
	void onRefreshAvailableCarsClick(ActionEvent event) throws RemoteException {

		cl = logicServer.getAvailableCars();

		ObservableList<String> items = FXCollections.observableArrayList();
		for (Car car : cl.getList()) {
			items.add(car.toString());
		}

		lvAvailableCars.setItems(items);
	}

	@FXML
	void onSelectCar(ActionEvent event) throws RemoteException {
		selected = cl.getList().get(lvAvailableCars.getSelectionModel().getSelectedIndex());
		String selectedChassisNumber = selected.getChassisNumber();

		tfRegisterPart_chassisNumber.textProperty().set(selectedChassisNumber);
		tfFinishCar_chassisNumber.textProperty().set(selectedChassisNumber);

	}

	@FXML
	void onRegisterPart(ActionEvent event) throws RemoteException {
		try {
			String chassisNumber = tfRegisterPart_chassisNumber.getText();
			String type = tfRegisterPart_type.getText();
			double weight = Double.parseDouble(tfRegisterPart_weight.getText());

			// TODO ?? too many -1
			// maybe car constructor only chassis number
			Part part = new Part(-1, type, weight, new Car(chassisNumber, "-1", "-1", -1, -1, "-1"));

			String serverResponse = logicServer.validateRegisterPart(part);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Server Response");
			alert.setHeaderText(null);
			alert.setContentText(serverResponse);
			alert.showAndWait();

		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Invalid Input");
			alert.showAndWait();
		}

		// TODO fill tfPutPart_partId with ID
	}

	@FXML
	void onPutPart(ActionEvent event) throws RemoteException {

		try {
			int partId = Integer.parseInt(tfPutPart_partId.getText());
			int palletId = Integer.parseInt(tfPutPart_palletId.getText());

			String serverResponse = logicServer.validatePutPart(partId, palletId);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Server Response");
			alert.setHeaderText(null);
			alert.setContentText(serverResponse);
			alert.showAndWait();

		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Invalid Input");
			alert.showAndWait();
		}

	}

	@FXML
	void onFinishPallet(ActionEvent event) throws RemoteException {
		try {
			int palletId = Integer.parseInt(tfFinishPallet_palletId.getText());

			String serverResponse = logicServer.validateFinishPallet(palletId);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Server Response");
			alert.setHeaderText(null);
			alert.setContentText(serverResponse);
			alert.showAndWait();
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Invalid Input");
			alert.showAndWait();
		}

	}

	@FXML
	void onFinishCar(ActionEvent event) throws RemoteException {
		String chassisNumber = tfFinishCar_chassisNumber.getText();

		String serverResponse = logicServer.validateFinishDismantling(chassisNumber);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Server Response");
		alert.setHeaderText(null);
		alert.setContentText(serverResponse);
		alert.showAndWait();
	}
}
