/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;

import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.core.flows.QuestionFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.Disconnect;
import org.speakright.core.render.FormElement;
import org.speakright.core.render.InlineGrammar;
import org.speakright.core.render.Grammar;
import org.speakright.core.render.Prompt;
import org.speakright.core.render.PromptItem;
import org.speakright.core.render.PromptType;
import org.speakright.core.render.Question;
import org.speakright.core.render.SpeechForm;
import org.speakright.core.render.GrammarItem;


public class TestGrammar extends BaseTest{
	
	@Test public void sroUrl()
	{
		SRInstance run = CreateInstance(new SRApp(), "", "");
		run.locations().setProjectDir("c:\\apps\\app1");
		run.setSROBaseUrl("http://localhost/app1");
		
		String url = run.locations().makeFullGrammarUrl("$sro$/grammar/digits.grxml");
		assertEquals("url", "http://localhost/app1/grammar/digits.grxml", url);

		url = run.locations().makeFullPromptUrl("$SRO$/audio/abc.wav");
		assertEquals("url", "http://localhost/app1/audio/abc.wav", url);

		url = run.locations().makeFullSROUrl("$sro$/abc.xml");
		assertEquals("url", "http://localhost/app1/abc.xml", url);
		url = run.locations().makeFullSROUrl("abc.xml");
		assertEquals("url", "http://localhost/app1/abc.xml", url);
	}
	
	@Test public void inlineGram()
	{
		String words = "kenny cartman [ter phil] (mister hat) (big gay al) [wendy]";
		InlineGrammar gram = new InlineGrammar(words);
		ArrayList<String> L = gram.wordList();
		
//		for(String s : L) {
//			System.out.println(s);
//		}
			
		assertEquals("count", 6, L.size());
		chk(gram, 0, "kenny");
		chk(gram, 1, "cartman");
		chk(gram, 2, "[ter phil]");
		chk(gram, 3, "(mister hat)");
		chk(gram, 4, "(big gay al)");
		chk(gram, 5, "[wendy]");
	}
	
	void chk(InlineGrammar gram, int index, String expected)
	{
		ArrayList<String> L = gram.wordList();
		String s = L.get(index);
		assertEquals("w", s, expected);
	}

	@Test public void grammar()
	{
		String words = "kenny cartman [ter phil] (mister hat) (big gay al) [wendy]";
		Grammar gram = new Grammar("inline: " + words);
	}
	
	@Test public void gslGrammar()
	{
		m_useMockPageWriter = true;

		QuestionFlow flow = new QuestionFlow("abc.gram", "what size?");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage("abc.gram");
		assertEquals("grxml", false, m_item.isGRXML());
	}
	@Test public void xmlGrammar()
	{
		m_useMockPageWriter = true;

		QuestionFlow flow = new QuestionFlow("abc.grxml#Rule1", "what size?");
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage("abc.grxml#Rule1");
		assertEquals("grxml", true, m_item.isGRXML());
	}


	void ChkPage(String expected)
	{
		int count = 0;
		SpeechForm form = m_writer.m_form;
		for(FormElement el : form.m_fieldL) {
			if (el instanceof Question) {
				chkQuestion((Question)el, expected);
				count++;
			}
			else if (el instanceof Prompt) {
			}
			else if (el instanceof Disconnect) {
			}
		}
		assertEquals("count", 1, count);
	}
	
	GrammarItem m_item;
	void chkQuestion(Question quest, String expected)
	{
		Grammar gram = quest.m_grammarSet.voiceGrammar();
		String url = gram.m_item.getUrl();
		m_item = gram.m_item;

		assertEquals("gram", expected, url);
	}
}
