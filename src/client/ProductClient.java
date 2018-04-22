package client;

import java.rmi.RemoteException;
import client.controller.ProductClientController;
import client.view.ProductClientView;

public class ProductClient
{

   public static void main(String[] args) throws RemoteException
   {
	   ProductClientView view =  new ProductClientView();
	   ProductClientController controller =  new ProductClientController(view);
	   
	   controller.begin();
	   view.start(controller);
   }
}
