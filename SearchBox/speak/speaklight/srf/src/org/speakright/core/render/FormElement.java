/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

/**
 * Base class for anything we render on a form.
 * We use StringTemplate for the low-level voicexml formatting, but still
 * need form elements to represent important things on a speech page.
 * @author IanRaeLaptop
 *
 */
public class FormElement {

	/**
	 * Render any prompts in this element using the pipeline.
	 * @param pipeline
	 */
	public void renderPrompts(PromptPipeline pipeline)
	{}
	
	/**
	 * Render any grammars in this element using the pipeline.
	 * @param pipeline
	 */
	public void renderGrammars(GrammarPipeline pipeline)
	{}
}
