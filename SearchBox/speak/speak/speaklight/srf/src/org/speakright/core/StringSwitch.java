package org.speakright.core;

/**
 * Utility class to simulate C#'s ability to have switch statements of strings. 
 * @author IanRaeLaptop
 *
 */
public class StringSwitch {

	public static int indexOf(String[] ar, String s)
	{
		for(int i = 0; i < ar.length; i++) {
			if (ar[i].equals(s))
				return i;
		}
		return -1; //not found
	}
}
