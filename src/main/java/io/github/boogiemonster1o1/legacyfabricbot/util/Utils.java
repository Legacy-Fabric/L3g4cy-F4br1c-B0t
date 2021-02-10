package io.github.boogiemonster1o1.legacyfabricbot.util;

@SuppressWarnings("ManualMinMaxCalculation")
public class Utils {
	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}
}
