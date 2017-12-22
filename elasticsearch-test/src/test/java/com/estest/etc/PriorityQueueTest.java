package com.estest.etc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.PriorityQueue;

import org.junit.Before;
import org.junit.Test;

public class PriorityQueueTest {
	private String[] values;
	private String[] asc;
	private String[] desc;
	
	@Before
	public void setUp() {
		values = new String[] {
			"aa","ba","ab"
		};
		asc = new String[] {
			"aa","ab","ba"	
		};
		desc = new String[] {
			"ba","ab","aa"	
		};
	}
	
	@Test
	public void priorityQueue() {
		PriorityQueue<String> ascQue = new PriorityQueue<>();
		PriorityQueue<String> descQue = new PriorityQueue<>(Collections.reverseOrder());
		for(int i=0; i<values.length; i++) {
			ascQue.offer(values[i]);
			descQue.offer(values[i]);
		}
		
		for(int i=0; i<values.length; i++) {
			assertThat(ascQue.poll(), is(asc[i]));
			assertThat(descQue.poll(), is(desc[i]));
		}
		
	}
	
	
	

}
