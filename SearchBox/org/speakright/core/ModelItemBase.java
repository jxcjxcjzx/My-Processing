package org.speakright.core;

import java.io.Serializable;

/**
 * Base class for model items.  Each model item class defines get,set, and clear.
 * @author Ian Rae
 *
 */
public abstract class ModelItemBase implements IModelItem, Serializable {
	protected boolean m_isSet = false;
	protected IItemFormatter m_formatter = new DefaultItemFormatter(this);
	
	/**
	 * has the model's <code>set</code> method been called.
	 */
	public boolean isSet()
	{
		return m_isSet;
	}
	
	public String toString() 
	{
		return m_formatter.formatItem();
	}

	public IItemFormatter getFormatter()
	{
		return m_formatter;
	}
	
	public void setFormatter(IItemFormatter formatter)
	{
		m_formatter = formatter; //not type-safe. can set an int-formatter for a bool. fix later!!
	}
}
