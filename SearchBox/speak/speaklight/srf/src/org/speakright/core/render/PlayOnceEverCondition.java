/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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
