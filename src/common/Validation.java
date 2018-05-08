package common;

public class Validation {
	public static final String YEAR = "(19|20)[0-9]{2}"; 
	
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
}