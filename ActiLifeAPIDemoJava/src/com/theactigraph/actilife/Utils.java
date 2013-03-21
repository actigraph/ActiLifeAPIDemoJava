package com.theactigraph.actilife;

import java.util.Calendar;
import java.util.Date;

public class Utils {
	public static float round(float value, int decimals) {
		float shift = (float) Math.pow(10, decimals);
		return (float) (Math.round(value * shift) / shift);
	}
	
	public static Date getDateAddMinutesFromNow(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
}
