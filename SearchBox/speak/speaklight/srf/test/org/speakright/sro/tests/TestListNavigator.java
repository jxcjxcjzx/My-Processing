package org.speakright.sro.tests;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.*;

import org.junit.Test;
import org.speakright.core.*;
import org.speakright.sro.BaseSROQuestion;
import org.speakright.sro.SROCancelCommand;
import org.speakright.sro.SROConfirmYesNo;
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
public class TestListNavigator extends BaseTest {

	@Test public void test1()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "There are 2  flights in the list. first item: apple");
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "second item: banana"); 
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "There are no more items"); 
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "There are no more items"); 
		
		Proceed(run, "prev");
		ChkPage(PromptType.MAIN1, "first item: apple"); 
		
		Proceed(run, "prev");
		ChkPage(PromptType.MAIN1, "There are no previous items"); 
		
		Proceed(run, "prev");
		ChkPage(PromptType.MAIN1, "There are no previous items"); 
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "second item: banana"); 
		
		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
//		ChkTrail(run, "Lister;Lister");
	}
	
	@Test public void testCancelCmd()
	{
        SRApp flow = createApp();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "There are 2  flights in the list. first item: apple");
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "second item: banana"); 
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "There are no more items"); 
		
		Proceed(run, "cancel");
		
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
		
//		ChkTrail(run, "Lister;Lister");
	}
	
	@Test public void testModelItem()
	{
        SRApp flow = createAppWithModelItems(false);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "There is 1  flight in the list. first item: flight 415 to austin");
		
		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
	}
	
	@Test public void testModelItem2()
	{
        SRApp flow = createAppWithModelItems(false);
		TrailWrapper wrap1 = new TrailWrapper(flow);
		m_sroListNav.setItemFormatter(new MyFlightItem.FlightItemShortFormatter());
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "There is 1  flight in the list. first item: flight 415");
		
		Proceed(run, "");
		assertEquals("fail", false, run.isFailed());
		assertEquals("fin", true, run.isFinished());
		assertEquals("start", true, run.isStarted());
	}
	
	@Test public void testEmptyList()
	{
        SRApp flow = createAppWithEmptyList();
		TrailWrapper wrap1 = new TrailWrapper(flow);
		
		SRInstance run = StartIt(wrap1);
		ChkPage(PromptType.MAIN1, "There are no flights in the list.");
		
		Proceed(run, "next");
		ChkPage(PromptType.MAIN1, "There are no more items"); 
		
		Proceed(run, "cancel");
		
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

	public static SRApp createApp()
	{
		return createApp(false);
	}
	
	static SROListNavigator m_sroListNav;
	public static SRApp createApp(boolean addConfirmer)
	{
        SRApp flow = new SRApp();

        ArrayList<String> L = new ArrayList<String>();
        L.add("apple");
        L.add("banana");
//        L.add("cherry");
//        SROListNavigator qflow = new SROListNavigator("flights", L);
        SROListNavigator qflow = new SROListNavigator("flights", L);
        m_sroListNav = qflow;
        qflow.setName("Lister");
        qflow.setModelVar("city");
        if (addConfirmer) {
        	qflow.setConfirmer(new SROConfirmYesNo("wine"));
        }
        qflow.addCommand("cancel", new SROCancelCommand());
        
		flow.add(qflow);

        return flow;
	}
	public static SRApp createAppWithModelItems(boolean addConfirmer)
	{
        SRApp flow = new SRApp();

        ArrayList<IModelItem> L = new ArrayList<IModelItem>();
        L.add(new MyFlightItem("austin", 415));
//        SROListNavigator qflow = new SROListNavigator("flights", L, 0);
        SROListNavigator qflow = new SROListNavigator("flights", L, 0);
        m_sroListNav = qflow;
        qflow.setName("Lister");
        qflow.setModelVar("city");
        if (addConfirmer) {
        	qflow.setConfirmer(new SROConfirmYesNo("wine"));
        }
		flow.add(qflow);

        return flow;
	}
	public static SRApp createAppWithEmptyList()
	{
        SRApp flow = new SRApp();

        ArrayList<String> L = new ArrayList<String>();
        SROListNavigator qflow = new SROListNavigator("flights", L);
        m_sroListNav = qflow;
        qflow.setName("Lister");
        qflow.setModelVar("city");
        qflow.addCommand("cancel", new SROCancelCommand());
        
		flow.add(qflow);
        return flow;
	}


	static public class MyFlightItem extends ModelItemBase implements Serializable {
		String m_destination;
		int m_flightNum;
		MyFlightItem(String destination, int flightNum) 
		{ 
			m_destination = destination; 
			m_flightNum = flightNum;
			initFormatter();
		}
		MyFlightItem() 
		{ 
			m_destination = ""; 
			initFormatter();
		}
		
		void initFormatter()
		{
			setFormatter(new FlightItemFormatter(this));
		}
		
		public Object rawValue()
		{
			return m_destination;
		}
		
		public void clear()
		{
			m_destination = "";
			m_flightNum = 0;
			m_isSet = false;
		}
		
		public String get() { return m_destination; }
		
		public void set(String val) 
		{
			m_isSet = true;
			m_destination = val; 
		}
				
		public class FlightItemFormatter implements IItemFormatter {
			private MyFlightItem m_item;
			
			public FlightItemFormatter(IModelItem item)
			{
				m_item = (MyFlightItem)item;
			}
			
			public String formatItem() {
				return String.format("flight %d to %s", m_flightNum, m_destination);
			}
	
		}
		public static class FlightItemShortFormatter implements IItemReusableFormatter {
			private MyFlightItem m_item;
			
			public FlightItemShortFormatter()
			{
			}
			
			public void setItem(IModelItem item)
			{
				m_item = (MyFlightItem)item;
			}
			
			public String formatItem() {
				return String.format("flight %d", m_item.m_flightNum);
			}
	
		}
		
//		public IItemFormatter createShortFormatter()
//		{
//			return new FlightItemShortFormatter(this);
//		}
	}
	
}
