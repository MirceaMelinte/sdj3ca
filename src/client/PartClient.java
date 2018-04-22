package client;

import java.rmi.RemoteException;
import client.controller.PartClientController;
import client.view.PartClientView;

public class PartClient
{

   public static void main(String[] args) throws RemoteException
   {
	   PartClientView view =  new PartClientView();
	   PartClientController controller =  new PartClientController(view);
	   
	   controller.begin();
	   view.start(controller);
   }
}
