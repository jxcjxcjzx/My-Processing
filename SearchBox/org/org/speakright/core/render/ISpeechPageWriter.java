package org.speakright.core.render;


/**
 * An ISpeechPageWriter generates the output text from speech page elements.
 * 
 * Speech page elements such as ISpeechForm are abstractions that hide
 * the target speech markup.  Currently VoiceXML is the only language
 * supported, but others can be added.
 *  
 * @author Ian Rae
 *
 */
public interface ISpeechPageWriter {

	void setRenderContext(RenderContext rcontext);
	void beginPage();

	/**
	 * Render the flow renderers.
	 * @param page the SpeechPage containing one or more forms
	 */
	void render(SpeechPage page);

	void endPage();
	
	String getContent();	
}