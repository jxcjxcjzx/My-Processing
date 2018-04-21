/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render.html;

import java.util.ArrayList;

import org.speakright.core.render.*;

/** 
 * wraps a Prompt object, for use in ST template
 * @author Ian Rae
 *
 */
public class PromptSpec {
	public PromptSpec(Prompt prompt) 
	{
		m_itemL.addAll(prompt.m_itemL);
		m_bargeIn = prompt.bargeIn();
	}
	
	ArrayList<PromptItem> m_itemL = new ArrayList<PromptItem>();
	boolean m_isFirst;
	int m_count;
	boolean m_bargeIn;
	
	public boolean getBargeIn()
	{
		return m_bargeIn;
	}
	
	public ArrayList getItemL() 
	{ 
		return m_itemL; 
	}
	public String getCountNot1() 
	{ 
		if (m_isFirst)
		return null;
		
		Integer n = m_count; 
		return n.toString();
	}
	
	static public void add(ArrayList L, PromptSpec spec)
	{
		spec.m_isFirst = (L.size() == 0);
		spec.m_count = L.size() + 1;
		L.add(spec);
	}
	
}
