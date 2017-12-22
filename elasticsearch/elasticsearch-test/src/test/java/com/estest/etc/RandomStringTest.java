package com.estest.etc;

import static org.junit.Assert.assertTrue;

import com.estest.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class RandomStringTest {	
	@Ignore
	@Test
	public void randomString() {
		List<String> values = new ArrayList<>();
		final int length = 5;
		int cnt = 10;
		
		int start = (int) 'a';
		int end = (int) 'z';
		char[] chars = new char[length];
		
		while (cnt-- > 0) {
			for(int i=0; i<chars.length; i++) {
				chars[i] = (char)randomValue(end-start+1,start); 
			}			
			values.add(new String(chars));
		}
		values.forEach(k->System.out.println(k));
	}

	@Test
	public void randomStringUtils() {
		int cases = 10;
		String prev = "aaaaa";
		while(cases-->0) {
			String cur = StringUtils.randomValue(5);
			System.out.println("prev : " + prev + ", cur : " + cur);
			System.out.println(prev.compareTo(cur));
			prev = cur;
		}	
	}
	

	@Test
	public void checkRandomValue() {
		int cases = 1000;
		while (cases-- > 0) {
			int random = randomValue(10, 11);
			assertTrue(random >= 11 && random <= 20);
		}
	}
	
	private int randomValue(int count, int start) {
		return (int) (Math.random() * count) + start;
	}

}
