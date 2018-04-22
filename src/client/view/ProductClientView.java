package client.view;

import java.rmi.RemoteException;
import java.util.Scanner;

import client.controller.CarClientController;
import client.controller.PartClientController;
import client.controller.ProductClientController;

public class ProductClientView {

	private Scanner in = new Scanner(System.in);

	public void start(ProductClientController controller) throws RemoteException {
		int menuSelection;
		do {
			menuSelection = menu();
			switch (menuSelection) {
			case 1:
				controller.execute("Add Product");
				break;
			case 2:
				controller.execute("List Parts");
				break;
			case 3:
				controller.execute("Quit");
				break;
			default:
				show("Invalid input");
				break;
			}
			show("\nPress ENTER to continue...");
			in.nextLine();
		} while (menuSelection != 3);
		
	}

	public void show(String value) {
		System.out.println(value);
	}
	
	public String get(String what) {
		System.out.print("Please enter " + what + ": ");
		String input = in.nextLine();
		return input;
	}

	private int menu() {
		System.out.println("Client 3: Product Registration");
		System.out.println("--------------");
		System.out.println("1) Add New Product");
		System.out.println("2) List Parts");
		System.out.println("3) Quit");
		System.out.println();
		System.out.print("Select an item 1-3: ");
		int selection = in.nextInt();
		in.nextLine();
		return selection;
	}

}
