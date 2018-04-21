package org.speakright.core;

import java.util.ArrayList;

import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;

/**
 * A flow that plays one or more prompts.
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
	 * @param text  prompt text
	 */
	public PromptFlow(String text)
	{
		super();	
		m_promptL.add(new Prompt(text));
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
	 * @param text prompt text, such as "Here is some music {willie.wav}"
	 */
	public void addPrompt(String text)
	{
		m_promptL.add(new Prompt(text));
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
