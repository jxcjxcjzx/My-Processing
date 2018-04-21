/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core.tests;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.*;
import java.io.*;

import org.speakright.core.*;
import org.speakright.core.render.*;
import org.speakright.core.tests.TestRender.ZFlow;

public class TestPrompts extends BaseTest implements IPromptAdjuster {

	@Test public void first()
	{
		chk("abc", 1, "T", "abc");
		chk("", 1, "T", "");
		
		//6Jun07: changed single period to mean ".".  It was being converted to a pause, which
		//made it hard to create a sentence "Question {%subject%}."
		chk(".", 1, "T", ".");
//		chk(".", 1, "T", null); //not tts
//		chk(".", 1, "P", "250"); //pause
		chk("..", 1, "P", "500"); //pause
		chk("abc{..}", 2, "TP", "abc", "500"); //pause
		chk("abc{..}def", 3, "TPT", "abc", "500", "def"); 
		chk("abc{def}.", 3, "TTT", "abc","def", ".");
	}
	
	void chk(String text, int count, String itemTypes, String expected)
	{
		chk(text, count, itemTypes, expected, "");
	}
	void chk(String text, int count, String itemTypes, String expected, String expected2)
	{
		chk(text, count, itemTypes, expected, expected2, "");
	}
	void chk(String text, int count, String itemTypes, String expected, String expected2, String expected3)
	{
		Prompt prompt = genPrompt(text);

		ArrayList L = prompt.m_itemL;
		for(int i = 0; i < L.size(); i++) {
			PromptItem item = (PromptItem)L.get(i);
			
			char ch = itemTypes.charAt(i);
			String target = null;
			if (expected != null && i == 0) {
				target = expected;
			}
			else if (expected2 != null && i == 1) {
				target = expected2;
			}
			else if (expected3 != null && i == 2) {
				target = expected3;
			}
			
			if (target != null) {
				switch(ch) {
				case 'T':
					assertEquals(String.format("[%d]", i), target, item.getTts());
					break;
				case 'P':
					assertEquals(String.format("[%d]", i), target, item.getPause());
					break;
				case 'A':
					assertEquals(String.format("[%d]", i), target, item.getAudio());
					break;
				default:
					assertEquals(String.format("[%d]", i), target, "UNKNOWN ITEM TYPE!");
				break;
				}
			}
		}
		assertEquals("size", count, L.size());
	}
	
	public String fixupPrompt(String item)
	{
		return null;
	}
	
	Prompt genPrompt(String text)
	{
		RenderContext rcontext = new RenderContext();
		rcontext.m_promptAdjuster = this;
//			rcontext.m_binder = m_binder;
		rcontext.m_flow = new App1();
//			rcontext.m_playOnceTracker = m_playOnceTracker;
		rcontext.m_locations = new SRLocations();
		rcontext.m_locations.setProjectDir(BaseTest.dir);
		rcontext.m_logger = SRLogger.createLogger();
		rcontext.m_promptFileL = new ArrayList<String>();
//			//copy both the permanent and temporary ones
//			rcontext.m_promptFileL.addAll(m_promptFileL);
//			rcontext.m_promptFileL.addAll(context.m_promptFileL);
//			rcontext.m_returnUrl = m_returnUrl;
//			rcontext.m_baseGrammarUrl = m_baseGrammarUrl;
//			rcontext.m_basePromptUrl = m_basePromptUrl;
		rcontext.m_doAudioMatching = true;
		
		PromptPipeline pipeline = new PromptPipeline(rcontext);
		Prompt prompt = new Prompt(text);
		pipeline.render(prompt);
		return prompt;
	}
	
	@Test public void audioMatch()
	{
//		Prompt prompt = genPrompt("silver"); //item that's in audiomatch.xml
		chk("silver", 1, "A", "silver1.wav");
		
		chk("welcome to Al's Airlines", 1, "A", "g73.wav");
		chk("WELCOME to Al's Airlines", 1, "A", "g73.wav");
		//punctuation still fools it!! fix!!
//		chk("welcome, to Al's Airlines", 1, "A", "g73.wav");
//		chk("welcome, to Al's airlines.", 1, "A", "g73.wav");
		
//		chk(s, "xsilver1.wav"); 
	}
}
