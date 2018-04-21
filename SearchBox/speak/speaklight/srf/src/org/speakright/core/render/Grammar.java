/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import java.io.Serializable;
import java.util.*;

import org.speakright.core.IFlow;
import org.speakright.core.ModelBinder;
import org.speakright.core.ModelBindingSpec;

/**
 * Represent a VoiceXML grammar.  SpeakRight supports three types of
 * grammars:  external grammars (referenced by URL), built-in grammars, and
 * inline grammars (which use a simplified GSL format).
 * 
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class Grammar extends FormElement implements Serializable {
	String m_gtext = ""; //url or builtin:xxxx or inline:xxxx
	GrammarType m_type = GrammarType.VOICE; //default
	String m_slotName = ""; //optional
	ArrayList<ModelBindingSpec> m_bindingL = new ArrayList<ModelBindingSpec>(); //optional bindings of slot to model-var
	
	GrammarCondition m_condition = null;
	
	//itemlist..
	transient public GrammarItem m_item; //rendered
	
	public Grammar() {
	}
	
	/**
	 * Create.
	 * @param gtext if no prefix then assume it's a url. otherwise it can
	 * be "inline:" followed by inline grammar words, or "builtin:" followed by
	 * built-in grammar specification.
	 */
	public Grammar(String gtext) 
	{
		m_gtext = gtext;
	}
	public Grammar(String gtext, GrammarType type) 
	{
		m_gtext = gtext;
		m_type = type;
	}
	public Grammar(String gtext, String slotName)
	{
		this(gtext, GrammarType.VOICE);
		m_slotName = slotName;
	}
	public Grammar(String gtext, String slotName, String modelVar)
	{
		this(gtext, GrammarType.VOICE);
		addBinding(m_slotName, modelVar);
	}
	
	public void addBinding(String slotName, String modelVar)
	{
		ModelBindingSpec spec = new ModelBindingSpec();
		spec.m_flow = null; //will be set in grammarpipeline
		spec.m_slotName = slotName;
		spec.m_target = modelVar;
		
		m_bindingL.add(spec); //later avoid repeats!
		
		if (m_slotName == "") {
			m_slotName = slotName;
		}
	}
	
	/**
	 * Used internally by the grammar pipeline
	 * @return
	 */
	public ArrayList<ModelBindingSpec> bindingList()
	{
		return m_bindingL;
	}
	
	public String gtext()
	{
		return m_gtext;
	}
	public GrammarType type()
	{
		return m_type;
	}
	public boolean isDTMFGrammar()
	{
		return m_type == GrammarType.DTMF;
	}
	
	public String slotName()
	{
		return m_slotName;
	}
	
	/**
	 * Set the slotName.  Be careful if there is also a model binding for this grammar not
	 * to add a binding with one slot name and then use setSlotName to change it!
	 * @param slotName
	 */
	public void setSlotName(String slotName)
	{
		m_slotName = slotName;
	}
	
	@Override
	public void renderGrammars(GrammarPipeline pipeline)
	{
		pipeline.render(this);
	}

	public void setConditionNone()
	{
		m_condition = null;
	}
	public void setConditionDTMFOnlyMode()
	{
		m_condition = new DTMFOnlyModeGrammarCondition();
	}
	public void setConditionCustom(boolean b)
	{
		m_condition = new GrammarCondition(b);
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
	 * GrammarCondition is a base class.
	 * @author IanRaeLaptop
	 *
	 */
	@SuppressWarnings("serial")
	static public class GrammarCondition implements Serializable
	{
		protected boolean m_value;
		
		public GrammarCondition()
		{}
		public GrammarCondition(boolean b)
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
		public void calculate(Grammar grammar, RenderContext rcontext)
		{
		}
	}
	
}
