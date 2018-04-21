/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import org.speakright.core.IFlow;

/**
 * A grammar condition that is true when DTMF-only mode is on.
 * @author IanRaeLaptop
 *
 */
@SuppressWarnings("serial")
public class DTMFOnlyModeGrammarCondition extends Grammar.GrammarCondition {
	@Override public void calculate(Grammar grammar, RenderContext rcontext)
	{
		doCalculate(grammar, rcontext, false);
	}
	
	void doCalculate(Grammar grammar, RenderContext rcontext, boolean onceEver)
	{
		if (! rcontext.m_dtmfOnlyModeIsActive)
			m_value = true;
		else {
			m_value = grammar.isDTMFGrammar();
		}
	}

}
