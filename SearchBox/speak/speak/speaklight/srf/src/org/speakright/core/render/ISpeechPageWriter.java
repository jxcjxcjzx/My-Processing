/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
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