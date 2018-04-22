package client.view;

import java.rmi.RemoteException;
import java.util.Scanner;

import client.controller.PartClientController;

public class PartClientView {

	private Scanner in = new Scanner(System.in);

	public void start(PartClientController controller) throws RemoteException {
		int menuSelection;
		do {
			menuSelection = menu();
			switch (menuSelection) {
			case 1:
				controller.execute("Add Part");
				break;
			case 2:
				controller.execute("Add Pallet");
				break;
			case 3:
				controller.execute("Set Pallet As Finished");
				break;
			case 4:
				controller.execute("Quit");
				break;
			default:
				show("Invalid input");
				break;
			}
			show("\nPress ENTER to continue...");
			in.nextLine();
		} while (menuSelection != 4);
		
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
		System.out.println("Client 2: Part Registration");
		System.out.println("--------------");
		System.out.println("1) Add New Part");
		System.out.println("2) Add New Pallet");
		System.out.println("3) Set Pallet As Finished");
		System.out.println("4) Quit");
		System.out.println();
		System.out.print("Select an item 1-4: ");
		int selection = in.nextInt();
		in.nextLine();
		return selection;
	}

}
