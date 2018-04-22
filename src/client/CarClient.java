package client;

import java.rmi.RemoteException;
import client.controller.CarClientController;
import client.view.CarClientView;

public class CarClient
{

   public static void main(String[] args) throws RemoteException
   {
	   CarClientView view =  new CarClientView();
	   CarClientController controller =  new CarClientController(view);
	   
	   controller.begin();
	   view.start(controller);
   }
}
