/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render;

import org.speakright.core.flows.TransferFlow.TransferType;

/**
 * Raw VoiceXML content.
 * @author IanRaeLaptop
 *
 */
public class RawContent extends FormElement {

	public String m_content;

	public RawContent(String content)
	{
		m_content = content;
	}

	/**
	 * used by ST
	 * @return
	 */
	public String getContent()
	{
		return m_content;
	}
}
