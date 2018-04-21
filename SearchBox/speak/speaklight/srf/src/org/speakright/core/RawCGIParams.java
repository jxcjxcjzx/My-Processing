/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.util.*;

/**
 * Holds all the CGI params received in an HTTP GET or POST.
 * Used to build an SRResults object. 
 * @author IanRaeLaptop
 *
 */
public class RawCGIParams {

	LinkedHashMap<String,String> m_paramMap = new LinkedHashMap<String, String>(); //paramName, value pairs
	
	public RawCGIParams()
	{
		
	}
	
	public void add(String param, String value)
	{
		m_paramMap.put(param, value);
	}
	
	public Map getMap()
	{
		return m_paramMap;
	}
}
