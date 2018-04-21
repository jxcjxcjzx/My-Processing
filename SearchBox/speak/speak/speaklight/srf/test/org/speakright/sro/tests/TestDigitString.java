package org.speakright.sro.tests;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.*;

import org.junit.Test;
import org.speakright.core.*;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROCancelCommand;
import org.speakright.sro.SROConfirmYesNo;
import org.speakright.sro.SRODigitString;
import org.speakright.sro.SROListNavigator;
import org.speakright.sro.SROStringItem;
import org.speakright.sro.SROListNavigator.Command;
import org.speakright.sro.gen.genSROListNavigator;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.*;
import org.speakright.core.tests.Model;
import org.speakright.core.tests.TestConfirmation.ConfYNFlow;
import org.speakright.core.tests.TestWebServlet2.MyApp;
import org.speakright.core.tests.TestWebServlet2.PFlow;


/*
 * to do
 * -ordinal into an sro
 * -variations of listnav
 * -listitem
 * -formatter
 */
public class TestDigitString extends BaseTest {

	@Test public void test1()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "Please enter a 4 digit login");
		ChkGrammar(GrammarType.VOICE, "builtin:digits?length=4");
		
		Proceed(run, "2222");
//		ChkPage(PromptType.MAIN1, "second item: banana"); 
		
//		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
//		ChkTrail(run, "Lister;Lister");
	}
	
	void ChkPage(PromptType type, String expected)
	{
		int count = 0;
		SpeechForm form = m_writer.m_form;
		for(FormElement el : form.m_fieldL) {
			if (el instanceof Question) {
				chkQuestion((Question)el, type, expected);
				count++;
			}
			else if (el instanceof Prompt) {
	//			int i = form.m_fieldL.indexOf(el);
	//			boolean isLast = (i >= form.m_fieldL.size() - 1);
	//			renderPrompt((Prompt)el, isLast);
			}
			else if (el instanceof Disconnect) {
	//			renderDisconnect((Disconnect)el);
			}
		}
		assertEquals("count", 1, count);
	}
	
	void chkQuestion(Question quest, PromptType type, String expected)
	{
		Prompt prompt = quest.m_promptSet.find(type, 0);
		PromptItem item = prompt.m_itemL.get(0);
		String s = "";
		for(PromptItem ii : prompt.m_itemL) {
			log("ditem: " + ii.getTts());
			s = s + ii.getTts() + " ";
		}
		s = s.trim();
		assertEquals("prompt", expected, s);
	}

	void ChkGrammar(GrammarType type, String expected)
	{
		SpeechForm form = m_writer.m_form;
		for(FormElement el : form.m_fieldL) {
			if (el instanceof Question) {
				chkGrammar((Question)el, type, expected);
			}
		}
	}
	void chkGrammar(Question quest, GrammarType type, String expected)
	{
		Grammar gram = quest.grammar();
		assertEquals("x", type, gram.type());
		assertEquals("y", expected, gram.gtext());
	}
	
	public static SRApp createApp()
	{
		return createApp(false);
	}
	
	static SRODigitString m_sroListNav;
	public static SRApp createApp(boolean addConfirmer)
	{
        SRApp flow = new SRApp();

        SRODigitString qflow = new SRODigitString("login", 4);
        m_sroListNav = qflow;
        qflow.setName("Login");
        qflow.setModelVar("city");
//        qflow.addCommand("cancel", new SROCancelCommand());
        
		flow.add(qflow);

        return flow;
	}

}
