package org.speakright.sro.tests;
import static org.junit.Assert.assertEquals;
import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROConfirmYesNo;
import org.speakright.sro.SRONumber;
import org.speakright.sro.SROYesNo;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.*;
import org.speakright.core.tests.Model;
import org.speakright.core.tests.TestConfirmation.ConfYNFlow;
import org.speakright.core.tests.TestWebServlet2.MyApp;
import org.speakright.core.tests.TestWebServlet2.PFlow;


public class TestYesNo extends BaseTest {

	@Test public void test1()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "Would you like vote ?");
		
		Proceed(run, "yes", "result"); //slotname result
		Proceed(run, "");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "MyYesNo;PFlow");
		assertEquals("city", "yes", m_app.M.city().get());
	}
	
	
	@Test public void testPromptGroupPrefix()
	{
		this.log("GROUPID..");
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
//		SRInstance run = StartIt(wrap1);
		SRInstance run = CreateInstance(wrap1, "");

		String path = dir + "sroapp_prompts.xml";
		run.registerPromptFile(path);
		
		boolean b = run.start(flow);		
		Assert.assertTrue(b);
		ChkPage(PromptType.MAIN1, "this is from the xml file.");
		
		Proceed(run, "yes", "result"); //slotname result
		Proceed(run, "");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "MyYesNo;PFlow");
		assertEquals("city", "yes", m_app.M.city().get());
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
		assertEquals("prompt", expected, item.getTts());
	}

	static SROYesNo m_yesno;
	static YesNoApp m_app;
	public static SRApp createApp()
	{
		YesNoApp flow = new YesNoApp();
		m_app = flow;
		
        SROYesNo qflow = new SROYesNo("vote");
        m_yesno = qflow;
        qflow.setName("MyYesNo");
        qflow.setModelVar("city");
		flow.add(qflow);

		flow.add(new PFlow("You said {$INPUT}"));
        return flow;
	}
	
	public static class YesNoApp extends SRApp {
		public Model M;
	}
}
