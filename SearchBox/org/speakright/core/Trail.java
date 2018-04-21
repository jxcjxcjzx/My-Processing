package org.speakright.core;
import java.io.Serializable;

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
