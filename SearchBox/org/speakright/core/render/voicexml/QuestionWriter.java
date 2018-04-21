package org.speakright.core.render.voicexml;

import java.util.ArrayList;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.speakright.core.render.*;

/*
 * field name="PhoneNumber">

    <prompt>What's your phone number?</prompt>

    <grammar src="../grammars/phone.gram" type="application/srgs+xml" />

    <help> Please say your ten digit phone number. </help>

<prompt count="1">
How many are travelling to <value expr="city"/>? 
</prompt>
</field>

<nomatch>
<prompt>Please say just a number.</prompt>
<reprompt/>
</nomatch>       

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
			PromptSpec spec = new PromptSpec(prompt);
			PromptSpec.add(result, spec);
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
				
		
//		field(hasInput, grammarUrl,dtmfGrammarUrl,
//		          promptL,silPromptL,norecoPromptL,nextUrl) ::= <<
		t.setAttribute("hasInput", true);
		t.setAttribute("dieCount", quest.m_maxAttempts);
		GrammarItem item = quest.grammar().m_item;
		System.out.println("zzzzgram " + item.getUrl());
		t.setAttribute("gram", item); //GrammarItem
//		t.setAttribute("grammarUrl", quest.m_gram.Url());
		t.setAttribute("dtmfGrammarUrl", null); //"def.grxml");
		t.setAttribute("fieldType", item.getBuiltIn()); //for builtin grammars


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
