package org.gjp.ldap.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class DateUtil {
	
	public static final String YYYYMMDD = "yyyyMMdd";
	
	public static Date dateToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDD);
		Date dateToday = new Date();
		try {
			dateToday = dateFormat.parse(dateFormat.format(dateToday));
		} catch(ParseException e) {
			throw new RuntimeException("Error occurred while parsing the date string into format:" + dateFormat, e);
		}
		return dateToday;
	}
	
	public static Date toDateFromYYYYMMDD(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDD);
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException("Error occurred while parsing the date string into format:" + YYYYMMDD, e);
		}
	}
	
	public static String dateStatus(String closeDate) {
		
			int value = dateToday().compareTo(toDateFromYYYYMMDD(closeDate));
			switch (value) {
			case 1:
				return "PAST";
			case 0:
				return "TODAY";
			case -1:
				return "FUTURE";
			default:
				return null;
			}
		}
	
	public static boolean closeDateValidation(String closeDate) {
		boolean result = false;
		if (null == closeDate || "FUTURE".equals(dateStatus(closeDate)))
			result=true;
		return result;
			
	}
}
