/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render.voicexml;
import java.util.ArrayList;

import org.speakright.core.render.*;
import org.antlr.stringtemplate.*;

/**
 * Renders a Transfer into a VoiceXML transfer tag.
 * @author IanRaeLaptop
 */
public class TransferWriter {

	VoiceXMLSpeechPageWriter m_pageWriter;
	
	public TransferWriter(VoiceXMLSpeechPageWriter pageWriter)
	{
		m_pageWriter = pageWriter;
	}
	
	void o(String s)
	{
		m_pageWriter.o(s);
	}
	

	public void render(Transfer xfer, String returnUrl) 
	{
		StringTemplateGroup group = m_pageWriter.m_templateGroup;

		StringTemplate t;
		t = group.getInstanceOf("transferTag");
		
		//transferTag(xfer,name,type,conntimeout,url) ::= <<
		t.setAttribute("xfer", xfer);
		t.setAttribute("name", "xfer1");
		t.setAttribute("conntimeout", xfer.m_connectionTimeout);
		String type = xfer.m_transferType.toString().toLowerCase();
		t.setAttribute("type", type);
		t.setAttribute("url", returnUrl);

		String str = t.toString();
		o(str);
	}

}
