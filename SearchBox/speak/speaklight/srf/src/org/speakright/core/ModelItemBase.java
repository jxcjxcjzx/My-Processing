/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

import java.io.Serializable;

/**
 * Base class for model items.  Each model item class defines get,set, and clear.
 * 
 * The recommended way to get a model item's value is using its formatter. getFormatter()->formatItem().
 * Do not use toString (may cause stack overflow since DefaultItemFormatter uses toString as well!)
 * @author Ian Rae
 *
 */
public abstract class ModelItemBase implements IModelItem, Serializable {
	protected boolean m_isSet = false;
	protected IItemFormatter m_formatter = new DefaultItemFormatter(this);
	protected String m_name = ""; //optional. used for logging
	
	/**
	 * has the model's <code>set</code> method been called.
	 */
	public boolean isSet()
	{
		return m_isSet;
	}
	
	public IItemFormatter getFormatter()
	{
		return m_formatter;
	}

	/**
	 * convenience function, equivalent to getFormatter->formatItem();
	 * @return
	 */
	public String getFormattedItem()
	{
		return m_formatter.formatItem();
	}
	
	public void setFormatter(IItemFormatter formatter)
	{
		m_formatter = formatter; //not type-safe. can set an int-formatter for a bool. fix later!!
	}
	
	protected void log(String s)
	{
		SRLogger logger = SRLogger.createLogger();
		logger.log("MODEL: " + s);
	}
}
