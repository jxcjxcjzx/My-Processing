package org.speakright.core.render;


/**
 * An IFlowRenderer renders a flow object into speech page elements.
 * Generally each IFlow-derived class defines its own flow renderer
 * as a private class.
 * 
 * Speech page elements such as ISpeechForm are abstractions that hide
 * the target speech markup.  Currently VoiceXML is the only language
 * supported, but others can be added.
 *  
 * @author Ian Rae
 *
 */
public interface IFlowRenderer {

	/**
	 * Render the flow into speech page elements.
	 * @param page The speech page being generated.
	 * @param form The current form on the speech page.
	 */
	void Render(ISpeechPage page, ISpeechForm form);
}
