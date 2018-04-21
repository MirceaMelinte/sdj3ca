package client.view;

import java.rmi.RemoteException;
import java.util.Scanner;

import client.controller.CarClientController;

public class CarClientView {

	private Scanner in = new Scanner(System.in);

	public void start(CarClientController controller) throws RemoteException {
		int menuSelection;
		do {
			menuSelection = menu();
			switch (menuSelection) {
			case 1:
				controller.execute("Add Car");
				break;
			case 2:
				controller.execute("Quit");
				break;
			default:
				show("Invalid input");
				break;
			}
			show("\nPress ENTER to continue...");
			in.nextLine();
		} while (menuSelection != 2);
		
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
		System.out.println("Client 1: Car Registration");
		System.out.println("--------------");
		System.out.println("1) Add New Car");
		System.out.println("2) Quit");
		System.out.println();
		System.out.print("Select an item 1-2: ");
		int selection = in.nextInt();
		in.nextLine();
		return selection;
	}

}
