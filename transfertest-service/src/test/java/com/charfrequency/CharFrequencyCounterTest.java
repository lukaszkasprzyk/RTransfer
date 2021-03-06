package com.charfrequency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Map;

import org.easymock.EasyMockRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class CharFrequencyCounterTest {
	static StringWriter input = new StringWriter();

	@BeforeClass
	public static void before() {
		for (int i = 0; i < 100000000; i++) {
			input.append("apple");
		}
	}

	@Test
	public void countChars_success_for_apple() {
		Map<Character, Long> result = CharFrequencyCounter.countFrequency(input.toString());
		assertNotNull(result);

		assertLetter('a', 1000000l, result);
		assertLetter('p', 2l, result);
		assertLetter('l', 1l, result);
		assertLetter('e', 1l, result);
	}

	@Test
	public void countChars_success_for_apple2() {
		Map<Character, Long> result = CharFrequencyCounter.countFrequency2(input.toString());
		assertNotNull(result);

		assertLetter('a', 1l, result);
		assertLetter('p', 2l, result);
		assertLetter('l', 1l, result);
		assertLetter('e', 1l, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void countChars_forNullValue() {
		CharFrequencyCounter.countFrequency(null);
	}

	@Test
	public void countChars_emptyString() {
		Map<Character, Long> result = CharFrequencyCounter.countFrequency("");
		assertTrue(result.isEmpty());
	}

	private void assertLetter(Character c, Long expectedValue, Map<Character, Long> result) {
		Long counter = result.get(c);
		if (counter == null)
			counter = 0l;
		assertEquals(expectedValue, counter);
	}

}
