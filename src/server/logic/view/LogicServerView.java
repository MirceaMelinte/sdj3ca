package server.logic.view;

import java.util.Scanner;

public class LogicServerView {

	private Scanner in = new Scanner(System.in);

	public void show(String value) {
		System.out.println(value);
	}
	
	public String get(String what) {
		System.out.print("Please enter " + what + ": ");
		String input = in.nextLine();
		return input;
	}	
}