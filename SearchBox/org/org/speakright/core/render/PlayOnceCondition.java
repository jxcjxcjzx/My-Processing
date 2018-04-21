package org.speakright.core.render;
import org.speakright.core.IFlow;

/**
 * A prompt condition that ensures a prompt is only played once.
 * Often in a VUI we want to play a prompt the first time the user reaches
 * some point, but not every time.  
 * 
 * The scope of this condition is a single activation
 * of a flow object.  An active object may have its execute method called one
 * or more times.  This condition ensures the prompt only is generated on the 
 * first execute.
 * @author IanRaeLaptop
 *
 */
public class PlayOnceCondition extends Prompt.PromptCondition {

	@Override public void calculate(Prompt prompt, RenderContext rcontext)
	{
		doCalculate(prompt, rcontext, false);
	}
	
	void doCalculate(Prompt prompt, RenderContext rcontext, boolean onceEver)
	{
		String text = prompt.text();
		IFlow flow = rcontext.m_flow;
		if (rcontext.m_playOnceTracker.find(flow, text) != null) {
			m_value = false;
		}
		else
		{
			m_value = true;
			rcontext.m_playOnceTracker.add(flow, prompt.text(), onceEver);
		}
	}
}
