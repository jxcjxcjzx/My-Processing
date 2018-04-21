package org.speakright.sro;

import java.io.Serializable;
import org.speakright.core.IExecutionContext;
import org.speakright.core.IItemFormatter;
import org.speakright.core.ModelItemBase;

/**
 * Item for saying the noun in a numerical noun phrase such the word "cars" in "two cars".
 * This SRO manages the singular/plural change on the noun "one car" vs "five cars" 
 * @author IanRaeLaptop
 *
 */
public class SROSubjectItem extends ModelItemBase implements Serializable {
	String m_value;
	int m_num;
	boolean m_outputNumber;
	
	/**
	 * Constructor
	 * @param val  plural version of the noun
	 * @param num  numerical value of the noun phrase
	 */
	public SROSubjectItem(String val, int num) 
	{ 
		this(val, num, false); //default is just to output the noun (m_value)
	}
	
	/**
	 * Constructor
	 * @param val  plural version of the noun
	 * @param num  numerical value of the noun phrase
	 * @param outputNumber  whether to output the number as well as the noun.  
	 */
	public SROSubjectItem(String val, int num, boolean outputNumber) 
	{ 
		m_value = val; 
		m_num = num;
		m_outputNumber = outputNumber;
		m_formatter = new SubjectItemFormatter();
	}
	
	public Object rawValue()
	{
		return m_value;
	}
	
	public void clear()
	{
		m_value = "";
		m_num = 0;
		m_isSet = false;
	}
	
	public String get() { return m_value; }
	
	public void set(String val) 
	{
		m_isSet = true;
		m_value = val; 
	}
	public int getNum() { return m_num; }
	public void setNum(int num)
	{
		m_num = num;
	}
	public boolean getOutputNumber() { return m_outputNumber; }
	public void setOutputNumber(boolean b) 
	{
		m_outputNumber = b;
	}
	
	public class SubjectItemFormatter implements IItemFormatter {
		
		public SubjectItemFormatter()
		{}
		
		public String formatItem() 
		{
			String s = "";
			
			if (m_outputNumber) {
				s += String.format("%s ", m_num);
			}
			//later add lang-specific logic!!  
			if (m_num == 1) {
				if (m_value.endsWith("s")) {
					s += m_value.substring(0, m_value.length() - 1);
				}
				else {
					s += m_value;
				}
			}
			else {
				s += m_value;
			}
			
			return s;
		}
	}
}
