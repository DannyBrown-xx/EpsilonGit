package org.eclipse.epsilon.emc.git;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GitCalendar extends GregorianCalendar {
	private static final long serialVersionUID = 944446675222929074L;

	public int getDay() {
		return get(Calendar.DAY_OF_MONTH);
	}
	
	public int getMonth() {
		return get(Calendar.MONTH) + 1;
	}
	
	public int getYear() {
		return get(Calendar.YEAR);
	}
	
	public int getHours() {
		return get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinutes() {
		return get(Calendar.MINUTE);
	}
	
	public int getSeconds() {
		return get(Calendar.SECOND);
	}
	
	public int getMilliseconds() {
		return get(Calendar.MILLISECOND);
	}
	
	public GitCalendar addDays(int numberOfDays) {
		GitCalendar cal = (GitCalendar) this.clone();
		cal.add(Calendar.DAY_OF_MONTH, numberOfDays);
		return cal;
	}
	
	public GitCalendar getPreviousDay(String dayOfWeek) {
		switch(dayOfWeek.toLowerCase()) {
			case "monday":
				return dateOfLast(Calendar.MONDAY);
			case "tuesday":
				return dateOfLast(Calendar.TUESDAY);
			case "wednesday":
				return dateOfLast(Calendar.WEDNESDAY);
			case "thursday":
				return dateOfLast(Calendar.THURSDAY);
			case "friday":
				return dateOfLast(Calendar.FRIDAY);
			case "saturday":
				return dateOfLast(Calendar.SATURDAY);
			case "sunday":
				return dateOfLast(Calendar.SUNDAY);
			default:
				throw new IllegalArgumentException("dayOfWeek must be a valid day. E.g. \"Monday\" or \"Tuesday\"");
		}
	}
	
	public String getDayOfWeek() {
		switch(getDayOfWeekValue()) {
			case 1:
				return "Saturday";
			case 2:
				return "Sunday";
			case 3:
				return "Monday";
			case 4:
				return "Tuesday";
			case 5:
				return "Wednesday";
			case 6:
				return "Thursday";
			case 7:
				return "Friday";
			default:
				return null; //This will never happen
		}
	}
	
	public int getDayOfWeekValue() {
		return this.get(Calendar.DAY_OF_WEEK);
	}

	private GitCalendar dateOfLast(int day) {
		GitCalendar lastDay = (GitCalendar) this.clone();
		while (lastDay.get(Calendar.DAY_OF_WEEK) > day) {
			lastDay.add(Calendar.DATE, -1); // Subtract 1 day until same as day.
		}
		
		return lastDay;
	}
}
