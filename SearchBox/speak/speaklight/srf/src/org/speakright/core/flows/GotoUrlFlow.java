/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.flows;

import org.speakright.core.FlowBase;
import org.speakright.core.render.IFlowRenderer;
import org.speakright.core.render.ISpeechForm;
import org.speakright.core.render.ISpeechPage;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.GotoUrl;

/**
 * A flow object that redirects the VoiceXML browser to another URL.
 * Used to to go to some static VoiceXML pages, or to another application.
 * Generates the VoiceXML "goto" tag.
 * @author IanRaeLaptop
 *
 */
public class GotoUrlFlow extends FlowBase {
	String m_url; //where to redirect to
	
	public GotoUrlFlow() {
		this("");
	}
	public GotoUrlFlow(String url) {
		super("goto");
		m_url = url;
	}
	
	public String getUrl() { return m_url; }
	public void setUrl(String url) {
		m_url = url;
	}
		
	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new GotoUrlFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class GotoUrlFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			form.addField(new GotoUrl(m_url));
		}
	}

}
