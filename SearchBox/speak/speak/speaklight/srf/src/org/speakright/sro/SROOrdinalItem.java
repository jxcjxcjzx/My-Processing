package org.speakright.sro;

import java.io.Serializable;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IItemFormatter;
import org.speakright.core.ModelItemBase;

/**
 * Item for saying an ordinal number (eg. "seventh").
 * Implemented as a model item simply because it has the API
 * we need.
 * @author IanRaeLaptop
 *
 */
public class SROOrdinalItem extends ModelItemBase implements Serializable {
	int m_value;
	SROOrdinalItem(int val, IExecutionContext context) 
	{ 
		m_value = val; 
		m_formatter = new OrdinalItemFormatter();
		
		context.registerPromptFile("$sro$\\" + "SROOrdinalItem_prompts.xml");		
	}
//	SROOrdinalItem() { this(0); }
	
	public Object rawValue()
	{
		return m_value;
	}
	
	public void clear()
	{
		m_value = 0;
		m_isSet = false;
	}
	
	public int get() { return m_value; }
	
	public void set(int val) 
	{
		m_isSet = true;
		m_value = val; 
	}
	
	public class OrdinalItemFormatter implements IItemFormatter {
		
		public OrdinalItemFormatter()
		{}
		
		public String formatItem() 
		{
			//later add lang-specific logic!!  one-hundred-and-seventeenth
			return String.format("{id:ordinal_%d}", m_value);
		}
	}
}
