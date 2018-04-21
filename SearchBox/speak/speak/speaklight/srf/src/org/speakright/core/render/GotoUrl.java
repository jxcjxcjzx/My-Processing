/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

/**
 * Redirects the VoiceXML browser to another URL.
 * Used to to go to some static VoiceXML pages, or to another application.
 * Generates the VoiceXML "goto" tag.
 * @author IanRaeLaptop
 *
 */
public class GotoUrl extends FormElement {

	public String m_url; //where to redirect to

	public GotoUrl(String url)
	{
		m_url = url;
	}

	/**
	 * used by ST
	 * @return
	 */
	public String getUrl()
	{
		return m_url;
	}
}
