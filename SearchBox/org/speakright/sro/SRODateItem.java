package org.speakright.sro;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.*;
import org.speakright.core.*;

public class SRODateItem extends ModelItemBase implements IItemFormatter {

	Date m_value;
	String m_dateFormat = "MMMM d";
	
	public SRODateItem(Date val) 
	{ 
		m_value = val;
		m_formatter = this; //we render ourselves
	}
	public SRODateItem() 
	{ 
		this(new Date()); 
	}
	
	public String getDateFormat()
	{
		return m_dateFormat;
	}
	public void setDateFormat(String fmt)
	{
		m_dateFormat = fmt;
	}
	
	public Object rawValue()
	{
		return m_value;
	}
	
	public void clear()
	{
		m_value = new Date();
		m_isSet = false;
	}
	
	public Date get() { return m_value; }
	
	public void set(Date val) 
	{
		m_isSet = true;
		m_value = val; 
	}
	
	public String formatItem() 
	{
		SimpleDateFormat sdf = new SimpleDateFormat(m_dateFormat);
		
		StringBuffer sb = sdf.format(m_value, new StringBuffer(), new FieldPosition(0));
		return sb.toString();
	}
}
