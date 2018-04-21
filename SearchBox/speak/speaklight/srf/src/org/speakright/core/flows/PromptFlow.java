/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;

import java.util.ArrayList;

import org.speakright.core.FlowBase;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;

/**
 * A flow that plays one or more prompts.
 * Note that type and sub-indexes are ignored here.  We have a list of prompts
 * that get rendered separately,  each with their own conditions applied.
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class PromptFlow extends FlowBase {

//	protected Prompt m_prompt;
	protected ArrayList<Prompt> m_promptL = new ArrayList<Prompt>();
	public PromptFlow()
	{
		Prompt prompt = new Prompt();
		m_promptL.add(prompt);
	}
		
	/**
	 * gets the bargein flag for the first
	 * prompt.  If you've added additional prompts,
	 * you need to get/set their bargein flags yourself.
	 * @return bargein flag for the first prompt
	 */
	public boolean bargeIn()
	{
		return firstPrompt().bargeIn();
	}
	
	/**
	 * sets the bargein flag for the first
	 * prompt.  If you've added additional prompts,
	 * you need to get/set their bargein flags yourself.
	 * @return bargein flag for the first prompt
	 * @param b bargeIn
	 */
	public void setBargeIn(boolean b)
	{
		firstPrompt().setBargeIn(b);
	}

	/**
	 * Careful -- param is the prompt text, not the name!
	 * @param ptext  prompt text
	 */
	public PromptFlow(String ptext)
	{
		super();	
		m_promptL.add(new Prompt(ptext));
	}
	
	public PromptFlow(String name, String ptext)
	{
		super();
		setName(name);
		m_promptL.add(new Prompt(ptext));
	}
	
	/**
	 * By default a PromptFlow has a single prompt.  
	 * @return returns the first propt.
	 */
	public Prompt firstPrompt()
	{
		return m_promptL.get(0); //should always be >= 1
	}
	
	/** 
	 * Adds additional prompt.
	 * @param ptext prompt text, such as "Here is some music {willie.wav}"
	 */
	public void addPrompt(String ptext)
	{
		//if the prompt added in the ctor is empty then replace it
		//This avoids empty <prompt> tags in the voicexml.
		if (m_promptL.size() == 1) {
			Prompt prompt = firstPrompt();
			if (prompt.ptext().length() == 0) {
				prompt.setPText(ptext);
				return;
			}
		}
		m_promptL.add(new Prompt(ptext));
	}
	
	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new PromptFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class PromptFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			for(Prompt prompt : m_promptL) {
				form.addField(prompt);
			}
		}
	}
}
