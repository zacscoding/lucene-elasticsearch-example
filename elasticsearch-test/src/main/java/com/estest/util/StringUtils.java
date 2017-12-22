package com.estest.util;

public class StringUtils {
	
	/**
	 * check String validation 
	 * 
	 * @author mskim
	 * @date 2017. 8. 10.
	 * @param value
	 * @return true if not null and not empty, false otherwise
	 */
	public static boolean isValid(String value) {
		return value != null && value.length() > 0;
	}
	
	/**
	 * generate to random string
	 * 
	 * @author mskim
	 * @date 2017. 8. 10.
	 * @param length random value`s length
	 * @return random string value or "" if length less than 1
	 */
	public static String randomValue(int length) {
		if(length < 1)
			return "";
		
		char[] chars = new char[length];		
		int cnt = (int)('z'-'a'); //26
		for(int i=0; i<chars.length; i++) {
			int randomValue = (int)(Math.random()*cnt);
			chars[i] = (char)(randomValue+'a');
		}
		
		return new String(chars);
	}
	
	

}
