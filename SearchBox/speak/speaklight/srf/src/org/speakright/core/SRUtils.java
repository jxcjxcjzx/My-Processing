package org.speakright.core;

/**
 * Holds commonly used stuff like safeToInt.
 * 
 * @author IanRaeLaptop
 *
 */
public class SRUtils {

	/**
	 * Convert string to int without exceptions being
	 * thrown.
	 * @param s  string to be converted, such as "15"
	 * @return integer value of s, 0 if conversion fails.
	 */
	public static int safeToInt(String s)
	{
		int n = 0;
		try {
			n = Integer.parseInt(s);
		}
		catch(Exception e)
		{
			
		}
		return n;
	}

	/**
	 * Convert string to int without exceptions being
	 * thrown.
	 * @param s  string to be converted, such as "15"
	 * @return integer value of s, 0 if conversion fails.
	 */
	public static double safeToDouble(String s)
	{
		double d = 0.0;
		try {
			d = Double.parseDouble(s);
		}
		catch(Exception e)
		{
			
		}
		return d;
	}
}
