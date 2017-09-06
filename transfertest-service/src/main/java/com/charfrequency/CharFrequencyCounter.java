package com.charfrequency;

import java.util.HashMap;
import java.util.Map;

public class CharFrequencyCounter {

	public static Map<Character, Long> countFrequency(String input) {
		if (input == null)
			throw new IllegalArgumentException("input cannot be null");
		Map<Character, Long> result = new HashMap<>();
		for (Character c : input.toCharArray()) {
			if (result.get(c) != null) {
				result.put(c, result.get(c) + 1);
			} else {
				result.put(c, 1l);
			}
		}
		return result;
	}

}
