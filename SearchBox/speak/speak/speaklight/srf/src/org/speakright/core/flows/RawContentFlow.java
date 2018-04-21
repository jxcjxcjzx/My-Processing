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
import org.speakright.core.render.RawContent;
import org.speakright.core.render.Transfer;

/**
 * A call control flow object that outputs raw VoiceXML.  This flow object is an escape hatch which apps
 * can use to output things not supported by SpeakRight.
 * The content supplied by the app is an entire VoiceXML page, starting with the <vxml> tags.
 * 
 * By default this flow outputs the <?xml?> tag.  But this can be disabled using setOutputXMLTag to false
 * and then including it in the content.
 * @author IanRaeLaptop
 *
 */
public class RawContentFlow extends FlowBase {
	
	String m_content;
	boolean m_outputXMLTag; //output the <?xml?> tag
	
	public RawContentFlow() {
		this("");
	}
	
	public RawContentFlow(String content) {
		super("raw");
		m_content = content;
		m_outputXMLTag = true;
	}
	
	/**
	 * get the raw VoiceXML content
	 * @return content
	 */
	public String Content()
	{
		return m_content;
	}
	/**
	 * set the raw VoiceXML content
	 * @param content
	 */
	public void setContent(String content) 
	{
		m_content = content;
	}
	/**
	 * get the flag that controls output of the <?xml?> tag
	 * @return flag
	 */
	public boolean OutputXMLTag()
	{
		return m_outputXMLTag;
	}
	/**
	 * set the flag that controls output of the <?xml?> tag.
	 * The only use for setting it to false is if you don't want
	 * to use version 1.0 and UTF-8.
	 */
	public void setOutputXMLTag(boolean b)
	{
		m_outputXMLTag = b;
	}
	
		

	/**
	 * Create the type-specific renderer
	 */
	@Override public IFlowRenderer createRenderer()
	{
		return new RawContentFlowRenderer();
	}
	
	/**
	 * an renderer for this flow object. 
	 * @author Ian Rae
	 *
	 */
	private class RawContentFlowRenderer implements IFlowRenderer
	{
		public void Render(ISpeechPage page, ISpeechForm form)
		{
			String s = "";
			if (m_outputXMLTag) {
				s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			}
			s += m_content;
			form.addField(new RawContent(s));
		}
	}

}
