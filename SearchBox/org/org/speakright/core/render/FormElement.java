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
