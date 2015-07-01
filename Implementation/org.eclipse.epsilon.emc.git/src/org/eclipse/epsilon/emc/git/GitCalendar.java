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
}
