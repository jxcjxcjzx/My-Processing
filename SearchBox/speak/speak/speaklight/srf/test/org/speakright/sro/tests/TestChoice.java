package org.speakright.sro.tests;
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROConfirmYesNo;
import org.speakright.sro.SROChoice;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.*;
import org.speakright.core.tests.Model;


public class TestChoice extends BaseTest {

	@Test public void test1()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "What color would you like?");
		ChkPage(PromptType.SILENCE1, "I'm sorry I didn't hear anything. You can say a color such as red or blue  What color would you like?");
		
		Proceed(run, "red");
		
		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROChoice;PromptFlow");
//		assertEquals("city", "yes", flow.M.city().get());
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

		String s = "";
		for(PromptItem tmp : prompt.m_itemL) {
			s += tmp + " ";
		}
		s = s.trim();
		assertEquals("prompt", expected, s);
	}

	public static SRApp createApp()
	{
		return createApp(false);
	}
	
	static SROChoice m_sroChoice;
	public static SRApp createApp(boolean addConfirmer)
	{
        SRApp flow = new SRApp();

        ArrayList<String> L = new ArrayList<String>();
        L.add("red");
        L.add("blue");
        L.add("green");
        SROChoice sro = new SROChoice("color", L);
        m_sroChoice = sro;
        sro.setSampleValue1("red");
        sro.setSampleValue2("blue");
        
        sro.setModelVar("city");
        if (addConfirmer) {
//        	qflow.setConfirmer(new ConfYNFlow());
        	sro.setConfirmer(new SROConfirmYesNo("wine"));
        }
		flow.add(sro);

		flow.addPromptFlow("You said {$INPUT}");
        return flow;
	}

}
