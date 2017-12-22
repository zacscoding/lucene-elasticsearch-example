package com.estest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static String writeValueAsString(Object value) {
		try {
			return mapper.writeValueAsString(value);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
