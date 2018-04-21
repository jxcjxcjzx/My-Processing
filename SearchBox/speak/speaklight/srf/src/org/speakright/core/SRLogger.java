/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import org.apache.log4j.*;

/**
 * We use three levels: DEBUG, INFO and ERROR
 * Log4J loggers may be assigned levels. TRACE, DEBUG, INFO, WARN, ERROR and FATAL
 *  
 * @author Ian Rae
 *
 */
public class SRLogger {
	Logger m_logger;

	private SRLogger()
	{
	   m_logger = Logger.getLogger("srf"); //org.speakright.srf");
		   
	   //we have a log4j.properties file in the classpath (speakright\bin)
	   //that has the configuration for our logger
//		   BasicConfigurator.configure();
//	   String path = dir + "config.lcf";
//	   PropertyConfigurator.configure(path);
	}
	
	public void logDebug(String message)
	{
		m_logger.debug(message);
	}
	public void logDebug(Object o)
	{
		m_logger.debug(o);
	}
	public void log(String message)
	{
		m_logger.info(message);
	}
	public void log(Object o)
	{
		m_logger.info(o);
	}
	public void logError(String message)
	{
		m_logger.error(message);
	}
	public void logError(Object o)
	{
		m_logger.error(o);
	}
	
	public String limitString(String s, int len)
	{
		if (s.length() > len) {
			s = s.substring(0, len) + "...";
		}
		return s;
	}

	static SRLogger the_logger; //singleton
	/**
	 * not thread-safe!!
	 * @return
	 */
	public static SRLogger createLogger()
	{
		if (the_logger == null) {
			the_logger = new SRLogger();
		}
		return the_logger;
	}
}
