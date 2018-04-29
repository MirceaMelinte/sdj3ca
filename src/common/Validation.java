package common;

public class Validation {

	// 4 figure number 1900-2099 
	public static final String YEAR = "(19|20)[0-9]{2}"; 
	
	// Upper case, lower case letters, numbers, length 17
	public static final String CHASSIS_NUMBER = "[A-Za-z0-9]{8}"; 
	
	public static final String PALLET_ID = "[1-9][0-9]{3}";
	
	public static final String PART_ID = "[1-9][0-9]{7}";

	public static final String PRODUCT_ID = "[1-9][0-9]{7}";
	
	
	public static boolean validate(String input, String regex) {
		return input.matches(regex);
	}
	
	public static boolean validate(int input, String regex) {
		return Integer.toString(input).matches(regex);
	}
	
	public static void main(String[] args) {
		System.out.println(validate(1811, Validation.YEAR));
	}
}
