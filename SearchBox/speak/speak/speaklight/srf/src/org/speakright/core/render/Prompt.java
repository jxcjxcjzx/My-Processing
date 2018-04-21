/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents audio output (of any type).  This object turns into a single voicexml prompt tag.
 * But the prompt text of this object can contain may items, such as "Welcome {..}{ here is an ad }{ad1.wav}"
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class Prompt extends FormElement implements Serializable {

	PromptType m_type = PromptType.MAIN1;
	String m_ptext = "";
	int m_subIndex = 0; //index within current type
	
	PromptCondition m_condition = null;
	
	boolean m_bargeIn = true;

	//itemlist..
	transient public ArrayList<PromptItem> m_itemL = new ArrayList<PromptItem>(); //rendered prompt goes here
	
	public Prompt()
	{}
	public Prompt(String ptext)
	{
		this(PromptType.MAIN1, ptext);	
	}
	public Prompt(PromptType type, String ptext)
	{
		m_type = type;
		m_ptext = ptext;
	}
	public Prompt(PromptType type, String ptext, int subIndex)
	{
		m_type = type;
		m_ptext = ptext;
		m_subIndex = subIndex;
	}
	
	public String ptext()
	{
		return m_ptext;
	}
	public void setPText(String ptext)
	{
		m_ptext = ptext;
	}
	public PromptType type()
	{
		return m_type;
	}
	public int subIndex()
	{
		return m_subIndex;
	}
	public void setSubIndex(int subIndex)
	{
		m_subIndex = subIndex;
	}
	public void renderPrompts(PromptPipeline pipeline)
	{
		pipeline.render(this);
	}
	public boolean bargeIn() 
	{
		return m_bargeIn;
	}
	public void setBargeIn(boolean in) 
	{
		m_bargeIn = in;
	}
	
	
	public void setConditionNone()
	{
		m_condition = null;
	}
	public void setConditionPlayOnce()
	{
		m_condition = new PlayOnceCondition();
	}
	public void setConditionPlayOnceEver()
	{
		m_condition = new PlayOnceEverCondition();
	}
	public void setConditionPlayIf(String modelVar)
	{
		m_condition = null; //!!
	}
	public void setConditionPlayIfEmpty(String modelVar)
	{
		m_condition = null; //!!
	}
	public void setConditionPlayIfNotEmpty(String modelVar)
	{
		m_condition = null; //!!
	}
	public void setConditionCustom(boolean b)
	{
		m_condition = new PromptCondition(b);
	}
	
	public boolean applyCondition(RenderContext rcontext)
	{
		if (m_condition == null) {
			return true;
		}
		else
		{
			//apply condition, lookup model, et
			m_condition.calculate(this, rcontext);
			return m_condition.value();
		}
	}
	
	/**
	 * A prompt condition controls whether a prompt plays or not.  It's common in VUIs to have some prompts
	 * only play once (the first time), or only when some state exists (the user has logged in).
	 * PromptCondition is a base class.
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	static public class PromptCondition implements Serializable
	{
		protected boolean m_value;
		
		public PromptCondition()
		{}
		public PromptCondition(boolean b)
		{
			m_value = b;
		}
		
		public boolean value()
		{
			return m_value;
		}

		/**
		 * Calculate the current value of the condition.
		 * @param prompt prompt that has the condition
		 * @param rcontext rendering context
		 */
		public void calculate(Prompt prompt, RenderContext rcontext)
		{
		}
	}
	
}
