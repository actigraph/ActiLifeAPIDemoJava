package com.theactigraph.actilife;

public class Utils {
	public static float round(float value, int decimals) {
		float shift = (float) Math.pow(10, decimals);
		return (float) (Math.round(value * shift) / shift);
	}
}
