package com.estest.util;

import com.estest.model.Logs;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Temp document extractor
 * 
 * @author mskim
 * @date 2017. 8. 10.
 *
 */
public class DocumentMock {	
	public static String getIndex(Class<?> clazz) {
		if(clazz == Logs.class) {
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		
		return null;
	}
	
	public static String getType(Class<?> clazz) {
		if(clazz == Logs.class) {
			return "log";
		}
		
		return null;		
	}
	
	

}
