package server.logic;

import java.rmi.RemoteException;
import server.logic.view.LogicServerView;
import server.logic.controller.LogicServerController;

public class LogicServer  {
	
	public static void main(String[] args) throws RemoteException {
		
		LogicServerView view = new LogicServerView();
		LogicServerController controller = new LogicServerController(view);

		controller.begin();
	}
}