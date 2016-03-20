package service;

public class MyUtils {

	public static boolean validateString(String string){
		if(string.isEmpty() || string.equalsIgnoreCase("null")){
			return false;
		}
		return true;
	}
	
}
