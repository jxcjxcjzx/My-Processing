/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

/**
 * Interface that lets flow objects participate in the grammar rendering
 * done by PromptPipeline.
 * @author IanRaeLaptop
 *
 */
public interface IGrammarAdjuster {

	/**
	 * Make adjustments to the grammar.  This method is invoked at the beginning
	 * of the grammar pipeline for rendering grammars into voicexml.
	 * @param gram  A grammar object that is being rendered
	 * @return null if no adjustments have been made, else grammar
	 */
	Grammar fixupGrammar(Grammar gram);
}
