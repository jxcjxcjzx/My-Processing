package org.speakright.sro.tests;
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;
import org.speakright.core.*;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROConfirmYesNo;
import org.speakright.sro.SRONumber;
import org.speakright.core.flows.SRApp;
import org.speakright.core.render.*;
import org.speakright.core.tests.Model;
import org.speakright.core.tests.TestConfirmation.ConfYNFlow;
import org.speakright.core.tests.TestWebServlet2.MyApp;
import org.speakright.core.tests.TestWebServlet2.PFlow;


public class TestNumber extends BaseTest {

	@Test public void test1()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "How many bottles would you like?");
		
		Proceed(run, "40");
		ChkPage(PromptType.MAIN1, "Sorry, I'm looking for a number between"); //"That value is out of range. How many bottles would you like?");
		
		Proceed(run, "4");
		Proceed(run, "");
//		Proceed(run, "yes", "confirm");
//		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROQuantity;SROQuantity;PFlow");
//		assertEquals("city", "yes", flow.M.city().get());
	}
	
	@Test public void test11()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		TestNumber.m_sroNum.setMax(1000);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "How many bottles would you like?");
		
		Proceed(run, "40000");
		ChkPage(PromptType.MAIN1, "That value is out of range. How many bottles would you like?");
		
		Proceed(run, "4");
		Proceed(run, "");
//		Proceed(run, "yes", "confirm");
//		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROQuantity;SROQuantity;PFlow");
//		assertEquals("city", "yes", flow.M.city().get());
	}
	
	@Test public void testMaxReexecute()
	{
		log("--------testMaxReexecute--------");
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "40");
		Proceed(run, "40");
		Proceed(run, "40");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROQuantity;SROQuantity;SROQuantity");
//		assertEquals("city", "yes", flow.M.city().get());
	}

	@Test public void testConfirmYes()
	{
		log("--------testConfirmYes--------");
        SRApp flow = createApp(true);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "2", "num", 40); //quest
		Proceed(run, "yes");
		Proceed(run, "");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROQuantity;SROConfirmYesNo;PFlow");
		assertEquals("city", "2", M.city().get());
	}
	
	@Test public void testConfirmNo()
	{
		log("--------testConfirmNo--------");
        SRApp flow = createApp(true);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "2", "num", 40); //quest
		Proceed(run, "no");
		Proceed(run, "8", "num"); //quest
		Proceed(run, "");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROQuantity;SROConfirmYesNo;SROQuantity;PFlow");
		assertEquals("city", "8", M.city().get());

	}

	
	@Test public void model()
	{
		log("--------model--------");
        SRApp flow = createApp(true);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		Proceed(run, "7", "num"); //quest
		Proceed(run, "");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
		ChkTrail(run, "SROQuantity;PFlow");
		assertEquals("city", "7", M.city().get());
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

	public static SRApp createApp()
	{
		return createApp(false);
	}
	
	static SRONumber m_sroNum;
	public static SRApp createApp(boolean addConfirmer)
	{
        SRApp flow = new SRApp();

//        SROQuantity qflow = new SROQuantity("bottles", 1, 10);
        SRONumber qflow = new SRONumber("bottles", 1, 10);
        m_sroNum = qflow;
        qflow.setName("SROQuantity");
        qflow.setModelVar("city");
        if (addConfirmer) {
//        	qflow.setConfirmer(new ConfYNFlow());
        	qflow.setConfirmer(new SROConfirmYesNo("wine"));
        }
		flow.add(qflow);

		flow.add(new PFlow("You said {$INPUT}"));
        return flow;
	}


	@SuppressWarnings("serial")
	public static class genSROQuantity extends BaseSROQuestion {

		protected String m_outOfRangePrompt = "id:outOfRange"; //That value is out of range. {%main1Prompt%}";
		
		public genSROQuantity(String subject)
		{
			super(subject);

			m_main1Prompt = "id:main1"; //"How many {%subject%} would you like?";
		}
		
		@Override
		public void initPrompts(IExecutionContext context)
		{
			super.initPrompts(context);
			//load prompt set using our prompt fields 
			initPrompt(PromptType.MAIN1, m_main1Prompt);
		}
		
		@Override
		public void execute(IExecutionContext context) 
		{
			context.registerPromptFile("$sro$\\SROQuantity_prompts.xml");
			
			if (m_modelVar.length() > 0) {
				m_quest.grammar().addBinding("num", m_modelVar);
			}
			super.execute(context);
		}
	}


	@SuppressWarnings("serial")
	public static class SROQuantity extends genSROQuantity {
		protected int m_min;
		protected int m_max;

		public SROQuantity(String text)
		{
			this(text, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		public SROQuantity(String text, int min, int max)
		{			
			super(text);
			m_min = min;
			m_max = max;
		}
		
		@Override
		public void initPrompts(IExecutionContext context)
		{
			super.initPrompts(context);
			//load prompt set using our prompt fields 
			initPrompt(PromptType.MAIN1, (context.ValidateFailed()) ? m_outOfRangePrompt : m_main1Prompt);
		}
		
		@Override
		public boolean validateInput(String input, SRResults results) {
			int val = safeToInt(results.m_input);
			boolean b = (val >= m_min) && (val <= m_max);
			this.log("val on " + results.m_input);
			return b;
		}
		
		int safeToInt(String s)
		{
			int n = 0;
			try {
				n = Integer.parseInt(s);
			}
			catch(Exception e)
			{
				
			}
			return n;
		}
	}
}
