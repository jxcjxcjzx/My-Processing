/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.io.Serializable;

/**
 * Builds a string of items, separated by ';'.
 * Useful in unit tests when you're trying to determine if
 * a series of things occured.
 * 
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class Trail implements Serializable {

	public Trail()
	{	
	}
	String m_trail = "";
	
	public void add(String s)
	{
		if (m_trail == "")
			m_trail = s;
		else
			m_trail = m_trail + ";" + s;
	}
	
	public String toString()
	{
		return m_trail;
	}
	
	public void clear()
	{
		m_trail = "";
	}
}
