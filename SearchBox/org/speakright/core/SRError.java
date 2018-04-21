package org.speakright.core;
import java.util.ArrayList;

/**
 * Manages error tracking.  Various SpeakRight classes will
 * have SRError objects to track their own errors.  The failed
 * method merges errors into a parent SRError.
 * 
 * @author Ian Rae
 *
 */
public class SRError {
	String m_component = ""; //name of entity we're tracking errors for. for logging purposes only.
	ArrayList m_L = new ArrayList(); //list of ErrorSpec objects from oldest to newest
	
	public SRError()
	{}
	public SRError(String component)
	{
		m_component = component;
	}
	
	
	/**
	 * Reports an error.  This is the method used throughout SpeakRight
	 * to report errors.
	 * @param errorCode   ??
	 * @param description friendly description
	 */
	public void logError(int errorCode, String description)
	{
		ErrorSpec spec = new ErrorSpec();
		spec.m_component = m_component;
		spec.m_errorCode = errorCode;
		spec.m_description = description;
		m_L.add(spec);
		
		SRLogger logger = SRLogger.createLogger();
		logger.logError(spec);
	}
	
	/**
	 * Return true if an error has been reported.
	 * @return whether error has been reported.
	 */
	public boolean failure()
	{
		return (m_L.size() > 0);
	}
	
	/**
	 * Merges this objects errors with parent.
	 * @param parent A SRError object owned by a higher-level object
	 * @return whether errors have occured.
	 */
	public boolean failed(SRError parent)
	{
		if (failure()) {
			parent.m_L.addAll(m_L);
			return true;
		}
		return false;
	}
	
	public String toString()
	{
		String s = "";
		for (int i = 0; i < m_L.size(); i++) {
			ErrorSpec spec = (ErrorSpec)m_L.get(i);
			s += spec.toString();
		}
		return s;
	}
	
	class ErrorSpec 
	{
		public String m_component = ""; //name of entity we're tracking errors for. for logging purposes only.
		public int m_errorCode;
		public String m_description = "";

		public String toString()
		{
			String s = String.format("ERROR(%d) in %s: %s. ", m_errorCode, m_component, m_description);
			return s;
		}
	}
}
