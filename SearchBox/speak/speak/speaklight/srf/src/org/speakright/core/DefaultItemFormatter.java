/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;

/**
 * The default item formatter simply calls toString of the given
 * model item.
 * Therefore model item's should override toString, unless they always
 * supply their own formatter.
 * @author IanRaeLaptop
 *
 */
public class DefaultItemFormatter implements IItemFormatter {
	private IModelItem m_item;
	
	public DefaultItemFormatter(IModelItem item)
	{
		m_item = item;
	}
	
	public String formatItem() {
		return m_item.toString();
	}

}
