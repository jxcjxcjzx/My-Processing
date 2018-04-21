/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Reads property files.  Currently reads a single .property file
 * for the app.  Properties can be overriden (but only in unit tests)  
 * <p>Later we may hook this in with Jakarta Commons or DB-based config
 * @author IanRaeLaptop
 *
 */
public class SRConfig {

	static boolean have_initialized = false;
	static String the_path;
	static Properties the_properties;
	
	/**
	 * MUST be called the first SpeakRight method called in an application.
	 * The constructors of flow objects often get config properties, so the app needs
	 * to first init SRConfig.
	 * @param path full path to the properties file.  The path is normally the same as the project dir.
	 * @param fileName name of the properties file.
	 */
	synchronized public static void init(String path, String fileName)
	{
		if (! have_initialized) {
			have_initialized = true;
			the_path = SRLocations.fixupDir(path) + fileName;
		}
	}
	
	/**
	 * Path of the config file (eg. c:\apps\app1\app1.properties)
	 * @return path
	 */
	public static String configPath()
	{
		return the_path;
	}
	
	/**
	 * Reset so <code>init</code> needs to be called again.
	 * Only used by unit tests.
	 *
	 */
	synchronized public static void uninit()
	{
		have_initialized = false;		
		the_path = null;
	}
	
	static Hashtable<String, String> the_overrides = new Hashtable<String, String>();

	/**
	 * Add an override value for a property.  Calls to getProperty after this will return
	 * the override value.  The override is stored in memory; the property file is not modified.
	 * This method is NOT thread-safe, so generally only used by unit tests that need to tweak
	 * a property.
	 * @param propName
	 * @param val
	 */
	public static void overrideProperty(String propName, String val) {
		the_overrides.put(propName, val);
	}

	/**
	 * Get a property.  Look first in overrides then in property file.  Only
	 * loads the property file once.
	 * Throws IllegalStateException if <code>init</code> hasn't yet been called.
	 * @param propName
	 * @return value of the property or "" if not found.  Never returns null.
	 */
	synchronized public static String getProperty(String propName) {
		if (! have_initialized) {
			throw new IllegalStateException("SRConfig.init not been called.");
		}
		String override = the_overrides.get(propName);
		if (override != null) {
			return override;
		}

		if (the_properties == null) {
			the_properties = new Properties();
			try {
				the_properties.load(new FileInputStream(the_path));
			} catch (IOException e) {
				return "";
			}
		}

		String s = the_properties.getProperty(propName);
		return (s == null) ? "" : s;
	}

	/** 
	 * Get property as a boolean. expects "true" or "false"
	 * @param propName
	 * @return true if property equals "true" (case-sensitive).
	 */
	public static boolean getBooleanProperty(String propName) {
		String s = getProperty(propName).toLowerCase();
		return (s.equals("true"));
	}
}
