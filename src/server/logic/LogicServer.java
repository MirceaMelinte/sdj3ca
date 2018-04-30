package server.logic;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import client.controller.ProductClientController;
import client.view.ProductClientView;
import common.Validation;
import remote.interfaces.*;
import server.logic.view.LogicServerView;
import server.logic.controller.LogicServerController;

import model.*;
import model.cache.*;

public class LogicServer  {
	
	public static void main(String[] args) throws RemoteException {
		
		LogicServerView view = new LogicServerView();
		LogicServerController controller = new LogicServerController(view);

		controller.begin();
	}

}