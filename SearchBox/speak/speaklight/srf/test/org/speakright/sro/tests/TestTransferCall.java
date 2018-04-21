package org.speakright.sro.tests;
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROConfirmYesNo;
import org.speakright.sro.SRONumber;
import org.speakright.sro.SROTransferCall;
import org.speakright.core.flows.PromptFlow;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.*;
import org.speakright.core.tests.Model;
import org.speakright.core.tests.TestWebServlet2.MyApp;
import org.speakright.core.tests.TestWebServlet2.PFlow;


public class TestTransferCall extends BaseTest {

	@Test public void test1()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "aaa");
		this.log("zzzzzzzzzz");
		Proceed(run, ""); //xfer main prompt
		ChkPage(PromptType.MAIN1, "This call is being transferred.");

		Proceed(run, ""); //xfer finishes
		Proceed(run, ""); //b
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "PromptFlow;SROTransferCall;PromptFlow");
	}
	
	
	void ChkPage(PromptType type, String expected)
	{
		int count = 0;
		SpeechForm form = m_writer.m_form;
		for(FormElement el : form.m_fieldL) {
			if (el instanceof Question) {
//				chkQuestion((Question)el, type, expected);
				count++;
			}
			else if (el instanceof Prompt) {
				Prompt prompt = (Prompt)el;
				PromptItem item = prompt.m_itemL.get(0);
				assertEquals("prompt", expected, item.getTts());
				count++;
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

	public static SRApp createApp()
	{
		return createApp(false);
	}
	
	static SROTransferCall m_sroXfer;
	public static SRApp createApp(boolean addConfirmer)
	{
        SRApp flow = new SRApp();

        flow.add(new PromptFlow("aaa"));
        m_sroXfer = new SROTransferCall("333");
		flow.add(m_sroXfer);

		flow.add(new PromptFlow("bbb"));
        return flow;
	}
}
