package server.data;

import java.rmi.RemoteException;
import server.data.controller.DataServerController;

public class DataServer   {
	
	public static void main(String[] args) throws RemoteException {
		   DataServerController.getInstance().begin();

	}
}