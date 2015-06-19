package org.eclipse.epsilon.emc.git.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.epsilon.emc.git.people.Person;

public class EGLTools {
	public String escapeJSON(String toEscape) {
		toEscape = toEscape.replace("'", "\\'");
		toEscape = toEscape.replace("\"", "\\\"");
		return toEscape;
	}
	
	public String escapeHTML(String toEscape) {
		return StringEscapeUtils.escapeHtml4(toEscape);
	}
	
	private Map<Person, Integer> map = new HashMap<Person, Integer>(); 
	
	private int getRandomColourValue(Person p) {
		if(map.containsKey(p)) {
			return map.get(p);
		}
		else {
			// Generate Colour
			int hexValue = 0;
			Random random = new Random();
			do {
				hexValue = random.nextInt(16777215);
			} while(map.containsKey(hexValue));
			map.put(p, hexValue);
			return hexValue;
		}
		
	}
	
	public String getAccentColour(Person k) {		
		int colourValue = getRandomColourValue(k);
		return '#' + Integer.toHexString(colourValue);
	}
	
	public String getDarkAccentColour(Person k) {
		int colourValue = getRandomColourValue(k);
		//fix this to work on individual hex values
		return '#' + Integer.toHexString(colourValue);
	}
}
