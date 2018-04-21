/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.render.voicexml;

import java.util.ArrayList;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.speakright.core.render.*;

/**
 * Renders a Question into a VoiceXML field, with all its prompts and grammars.
 * @author IanRaeLaptop
 */
public class QuestionWriter {
	
	VoiceXMLSpeechPageWriter m_pageWriter;
	
	public QuestionWriter(VoiceXMLSpeechPageWriter pageWriter)
	{
		m_pageWriter = pageWriter;
	}
	
	void o(String s)
	{
		m_pageWriter.o(s);
	}
	
	ArrayList makePromptList(PromptSet promptSet, PromptType.Family family)
	{
		ArrayList tmp = promptSet.findAll(family); //list of Prompt objects
		
		ArrayList result = new ArrayList();
		for(int i = 0; i < tmp.size(); i++) {
			Prompt prompt = (Prompt)tmp.get(i);
			if (prompt.m_itemL.size() > 0) { //skip empty ones
				PromptSpec spec = new PromptSpec(prompt);
				PromptSpec.add(result, spec);
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @param grammarSet
	 * @return list of GrammarItems
	 */
	ArrayList makeGrammarList(GrammarSet grammarSet)
	{
		ArrayList tmp = grammarSet.Grammars();
		
		ArrayList result = new ArrayList();
		for(int i = 0; i < tmp.size(); i++) {
			Grammar gram = (Grammar)tmp.get(i);
			result.add(gram.m_item);
		}
		
		return result;
	}

	public void render(Question quest, int fieldNum) 
	{
//		o(String.format("<field name=\"field%d\"", fieldNum));
//		for(Prompt prompt : quest.m_promptSet.prompts()) {
//			m_pageWriter.renderPrompt(prompt);			
//		}
////		String s = String.format("question...%s", quest.m_gram.Url());
////		o(s);				
//		
//		o("</field>");
		
		StringTemplateGroup group = m_pageWriter.m_templateGroup;
		StringTemplate t = group.getInstanceOf("field");
				
		
		t.setAttribute("hasInput", true);
		t.setAttribute("dieCount", quest.m_maxAttempts);
		t.setAttribute("gramL", makeGrammarList(quest.m_grammarSet));
		
		GrammarItem item = quest.grammar().m_item;
		if (item == null) { //can be null in dtmfonly mode (the first grammar is usually voice, so won't render anything)
			t.setAttribute("fieldType", null);
		}
		else {
			t.setAttribute("fieldType", item.getBuiltIn()); //for builtin grammars
		}
		String slotName = quest.grammar().slotName();
		if (slotName == "") {
			slotName = "field1";
		}
		t.setAttribute("fld1", slotName);


		ArrayList promptL = makePromptList(quest.m_promptSet, PromptType.Family.MAIN);		
		t.setAttribute("promptL",  promptL);
		
		t.setAttribute("silPromptL", makePromptList(quest.m_promptSet, PromptType.Family.SILENCE));
		t.setAttribute("norecoPromptL", makePromptList(quest.m_promptSet, PromptType.Family.NORECO));
		String nextUrl = m_pageWriter.m_rcontext.m_returnUrl;
		t.setAttribute("nextUrl", nextUrl);
		
		String str = t.toString();
		o(str);
		
	}
}
