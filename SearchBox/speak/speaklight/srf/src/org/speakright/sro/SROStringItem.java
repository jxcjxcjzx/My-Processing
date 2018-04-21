package org.speakright.sro;

import java.io.Serializable;
import org.speakright.core.ModelItemBase;

/**
 * A string value, used in SROs such as SROListNavigator.
 * Even though we derived from ModelItemBase, these string values do not have
 * to be from the model.
 * 
 * @author IanRaeLaptop
 *
 */
public class SROStringItem extends ModelItemBase implements Serializable {
	String m_value;
	SROStringItem(String val) { m_value = val; }
	SROStringItem() { m_value = ""; }
	
	public Object rawValue()
	{
		return m_value;
	}
	
	public void clear()
	{
		m_value = "";
		m_isSet = false;
	}
	
	public String get() { return m_value; }
	
	public void set(String val) 
	{
		m_isSet = true;
		m_value = val; 
	}

	/**
	 * IModel items should override toString because the default item formatter
	 * uses toString.
	 * Return the value of this item as a ptext.  DO NOT invoke the formatter here
	 * or you'll get stack overflow.
	 */
	@Override public String toString()
	{
		return m_value;
	}
}
