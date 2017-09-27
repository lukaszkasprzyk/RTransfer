package com.charfrequency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class CharFrequencyCounter {

	public static Map<Character, Long> countFrequency(String input) {
		if (input == null)
			throw new IllegalArgumentException("input cannot be null");
		long time = System.currentTimeMillis();
		Map<Character, Long> result = new HashMap<>();
		for (Character c : input.toCharArray()) {
			if (result.get(c) != null) {
				result.put(c, result.get(c) + 1);
			} else {
				result.put(c, 1l);
			}
		}
		System.out.println("TIME1:" + (System.currentTimeMillis() - time));
		return result;
	}

	public static Map<Character, Long> countFrequency2(String input) {
		ReentrantLock lock = new ReentrantLock();

		
		if (input == null)
			throw new IllegalArgumentException("input cannot be null");
		long time = System.currentTimeMillis();
		Map<Character, Long> result = new HashMap<>();
		input.chars().forEach(obj -> {
			Character chr = (char)obj;
			result.put(chr, result.getOrDefault(chr, 1l) + 1);
		});

		System.out.println("TIME2:"+(System.currentTimeMillis() - time));
		return result;
	}
	
	@Test
	public void test1(){
		LongAccumulator ac = new LongAccumulator((a,b)->{return a+b;}, 1l);
		ac.accumulate(2);
		System.out.println(ac.get());
	}

}
