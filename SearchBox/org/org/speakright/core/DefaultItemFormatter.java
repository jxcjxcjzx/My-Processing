package org.speakright.core;

/**
 * The default item formatter simply calls toString of the given
 * model item.
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
