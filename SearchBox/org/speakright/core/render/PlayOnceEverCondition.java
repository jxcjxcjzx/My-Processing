package org.speakright.core.render;

/**
 * A prompt condition that ensures a prompt is only played once ever during a call.
 * Often in a VUI we want to play a prompt the first time the user reaches
 * some point, but not every time.  
 * The scope of this condition is the entire call. 
 * @author IanRaeLaptop
 *
 */
public class PlayOnceEverCondition extends PlayOnceCondition {

	@Override public void calculate(Prompt prompt, RenderContext rcontext)
	{
		doCalculate(prompt, rcontext, true);
	}

}
