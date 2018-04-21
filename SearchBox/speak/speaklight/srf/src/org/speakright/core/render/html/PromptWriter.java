/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render.html;
import java.util.ArrayList;

import org.speakright.core.render.*;
import org.antlr.stringtemplate.*;

/**
 * Renders a prompt into a HTML representation of it.
 * @author IanRaeLaptop
 */
public class PromptWriter {

	HTMLSpeechPageWriter m_pageWriter;
	//static String dir = "C:\\Source\\Eclipse\\SpeakRight\\test\\org\\speakright\\core\\tests\\testfiles\\";
	
	public PromptWriter(HTMLSpeechPageWriter pageWriter)
	{
		m_pageWriter = pageWriter;
	}
	
	void o(String s)
	{
		m_pageWriter.o(s);
	}
	

	public void render(Prompt prompt, boolean asBlock) 
	{
		StringTemplateGroup group = m_pageWriter.m_templateGroup;

		StringTemplate t;
		if (asBlock) {
			t = group.getInstanceOf("block");
		} else {
			t = group.getInstanceOf("rawPrompt");
		}
		ArrayList L = new ArrayList();
		
		PromptSpec spec = new PromptSpec(prompt);
		add(L, spec);
				
		t.setAttribute("promptL", L);
		String str = t.toString();
		o(str);
	}

	void add(ArrayList L, PromptSpec spec)
	{
		PromptSpec.add(L, spec);
	}
	
	public void renderSubmitBlock(String url)
	{
		StringTemplateGroup group = m_pageWriter.m_templateGroup;

		StringTemplate t = group.getInstanceOf("submitBlock");
				
		t.setAttribute("url", url);
		String str = t.toString();
		o(str);		
	}
	
}
