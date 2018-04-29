package client.controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import model.Car;
import remote.interfaces.ILogicServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CarClientController {

	private ILogicServer logicServer;

	public CarClientController() throws RemoteException, MalformedURLException, NotBoundException {
		 logicServer = (ILogicServer) Naming.lookup("rmi://localhost/logicServer");
	}

	@FXML
	private TextField tfYear, tfChassisNumber, tfModel, tfManufacturer, tfWeight;

	@FXML
	private Button btnRegister;

	@FXML
	void onRegisterClick(ActionEvent event) throws RemoteException {
		Car car;

		try {
			String chassisNumber = tfChassisNumber.getText();
			String manufacturer = tfManufacturer.getText();
			String model = tfModel.getText();
			int year = Integer.parseInt(tfYear.getText());
			double weight = Double.parseDouble(tfWeight.getText());
			String state = Car.AVAILABLE;
			
			car = new Car(chassisNumber, manufacturer, model, year, weight, state);

			String serverResponse = logicServer.validateRegisterCar(car);

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

}
