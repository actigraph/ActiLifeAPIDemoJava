package com.theactigraph.actilife;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Utils {
	public static float round(float value, int decimals) {
		float shift = (float) Math.pow(10, decimals);
		return (float) (Math.round(value * shift) / shift);
	}

	public static String getUTCNowPlusMinutes(int minutes) {
		return new DateTime(DateTimeZone.UTC).plusMinutes(minutes).toString();
	}
}
